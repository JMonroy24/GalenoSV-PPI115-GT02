
package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import jakarta.persistence.OptimisticLockException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Backing bean abstracto y genérico para vistas JSF con operaciones CRUD.
 * Usa el patrón Template Method: las subclases solo implementan
 * {@link #getDAO()} y {@link #crearNuevoRegistro()}.
 *
 * @param <T>  tipo de la entidad JPA
 * @param <ID> tipo de la llave primaria; debe ser Serializable
 *
 */
public abstract class Model<T, ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Model.class.getName());

    /**
     * Retorna el DAO concreto para las operaciones de persistencia.
     * @return la instancia del DAO; nunca debe ser null
     */
    protected abstract DAOInterface<T, ID> getDAO();

    /**
     * Crea una nueva instancia vacía de la entidad para el formulario.
     * @return nueva instancia de la entidad
     */
    protected abstract T crearNuevoRegistro();

    /** Lista de registros cargados desde la base de datos. */
    private List<T> registros;

    /** Registro seleccionado o en creación/edición; null si no hay operación activa. */
    private T registroActual;

    /** Fila seleccionada en la tabla. Separada de registroActual para que el
     *  procesamiento ajax de la tabla nunca destruya el objeto del formulario. */
    private T seleccion;

    /** Estado actual de la operación CRUD. */
    private Estado estado = Estado.NINGUNO;

    /**
     * Carga todos los registros de la entidad desde la base de datos.
     * Se invoca normalmente al inicializar la vista.
     */
    public void cargarDatos() {
        try {
            registros = getDAO().findAll();
        } catch (Exception e) {
            registrarError("cargar datos", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los datos.");
        }
    }

    /** Prepara un registro nuevo y cambia el estado a CREAR. */
    public void prepararNuevo() {
        registroActual = crearNuevoRegistro();
        estado = Estado.CREAR;
    }

    /**
     * Selecciona un registro existente para edición.
     * @param registro el registro seleccionado por el usuario
     */
    public void seleccionar(T registro) {
        if (registro == null) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Seleccione un registro para editar.");
            return;
        }
        registroActual = registro;
        estado = Estado.MODIFICAR;
    }

    /** Cancela la operación en curso y restablece el estado a NINGUNO. */
    public void cancelar() {
        registroActual = null;
        seleccion = null;
        estado = Estado.NINGUNO;
    }

    /**
     * Persiste o actualiza el registro actual según el estado actual.
     * Recarga datos y cancela la operación al finalizar exitosamente.
     */
    protected void validarNegocio(T registro) { }

    protected void persistirNuevo(T registro) { getDAO().create(registro); }
    protected void persistirCambios(T registro) { getDAO().update(registro); }

    public void guardar() {
        try {
            if (registroActual == null || (estado != Estado.CREAR && estado != Estado.MODIFICAR)) {
                agregarMensaje(FacesMessage.SEVERITY_WARN, "Aviso",
                        "No hay una operación de guardado activa. Abra el formulario con Nuevo o Editar.");
                marcarValidacionFallida();
                return;
            }
            validarNegocio(registroActual);
            if (estado == Estado.CREAR) {
                persistirNuevo(registroActual);
                agregarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Registro creado correctamente.");
            } else if (estado == Estado.MODIFICAR) {
                persistirCambios(registroActual);
                agregarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Registro actualizado correctamente.");
            }
            cargarDatos();
            cancelar(); 
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage());
            marcarValidacionFallida();
        } catch (Exception e) {
            registrarError("guardar registro", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Error", clasificarError(e));
            marcarValidacionFallida();
        }
    }

    /**
     * Elimina un registro de la base de datos y recarga la lista
     * @param registro el registro a eliminar
     */
    public void eliminar(T registro) {
        if (registro == null) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Seleccione un registro para eliminar.");
            return;
        }
        Estado estadoAnterior = estado;
        try {
            estado = Estado.ELIMINAR;
            getDAO().delete(registro);
            cargarDatos();
            if (registro != null && registro.equals(seleccion)) {
                seleccion = null;
            }
            if (registroActual != null && registro.equals(registroActual)) {
                cancelar();
            } else {
                estado = estadoAnterior;
            }
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Registro eliminado correctamente.");
        } catch (Exception e) {
            estado = estadoAnterior;
            registrarError("eliminar registro", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Error", clasificarError(e));
        }
    }

    // ─── Manejo de errores ───────────────────────────────────────────

    /**
     * Extrae y clasifica la causa raíz de una excepción JPA/PostgreSQL
     * para retornar un mensaje amigable al usuario.
     *
     * @param e excepción capturada
     * @return mensaje descriptivo
     */
    protected String clasificarError(Exception e) {
        Set<Throwable> visitadas = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Throwable causa = e; causa != null && visitadas.add(causa); causa = causa.getCause()) {
            if (causa instanceof ValidacionNegocioException) return causa.getMessage();
            if (causa instanceof ConstraintViolationException cve && !cve.getConstraintViolations().isEmpty()) {
                return cve.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                        .distinct().sorted().collect(Collectors.joining("; "));
            }
            if (causa instanceof OptimisticLockException) {
                return "Otro usuario modificó este registro. Vuelva a cargarlo y revise los cambios antes de guardar.";
            }
            if (causa instanceof SQLException sql) {
                Set<SQLException> sqlVisitadas = Collections.newSetFromMap(new IdentityHashMap<>());
                for (SQLException actual = sql; actual != null && sqlVisitadas.add(actual); actual = actual.getNextException()) {
                    String mensaje = mensajeSql(actual.getSQLState());
                    if (mensaje != null) return mensaje;
                }
            }
        }
        return "No fue posible completar la operación. Intente nuevamente o contacte al administrador.";
    }

    private String mensajeSql(String sqlState) {
        if (sqlState == null) return null;
        return switch (sqlState) {
            case "23505" -> "Ya existe un registro con estos datos. Verifique los campos que deben ser únicos.";
            case "23503" -> "No se puede completar la operación: este registro está relacionado con otros datos.";
            case "23502" -> "Faltan campos obligatorios. Complete todos los datos requeridos.";
            case "23514" -> "Los datos no cumplen las reglas requeridas. Revise las fechas y los valores ingresados.";
            case "22001" -> "Uno de los campos excede la longitud máxima permitida.";
            case "40001", "40P01", "55P03" -> "Otro usuario está modificando estos datos. Recargue el registro e intente nuevamente.";
            default -> null;
        };
    }

    private void registrarError(String operacion, Exception e) {
        // SQL puede incluir documentos o información clínica; registrar sólo el tipo del error.
        LOGGER.log(Level.WARNING, "No se pudo {0}. Tipo: {1}", new Object[]{operacion, e.getClass().getName()});
    }

    protected void marcarValidacionFallida() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        if (contexto != null) contexto.validationFailed();
    }

    protected void agregarMensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        if (contexto != null) contexto.addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    // ─── Estado ──────────────────────────────────────────────────────

    /** @return true si se está creando un registro */
    public boolean isEstadoCrear() { return estado == Estado.CREAR; }

    /** @return true si se está modificando un registro */
    public boolean isEstadoModificar() { return estado == Estado.MODIFICAR; }

    /** @return true si se está eliminando un registro */
    public boolean isEstadoEliminar() { return estado == Estado.ELIMINAR; }

    /** @return true si no hay una operación activa */
    public boolean isEstadoNinguno() { return estado == Estado.NINGUNO; }

    // ─── Accessors ───────────────────────────────────────────────────

    public List<T> getRegistros() { return registros; }
    public void setRegistros(List<T> registros) { this.registros = registros; }
    public T getRegistroActual() { return registroActual; }
    public void setRegistroActual(T registroActual) { this.registroActual = registroActual; }
    public T getSeleccion() { return seleccion; }
    public void setSeleccion(T seleccion) { this.seleccion = seleccion; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
