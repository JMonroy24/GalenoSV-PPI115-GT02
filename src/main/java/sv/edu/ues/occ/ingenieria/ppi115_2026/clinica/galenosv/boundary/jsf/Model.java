
package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;

/**
 * Backing bean abstracto y genérico para vistas JSF con operaciones CRUD.
 * Usa el patrón Template Method: las subclases solo implementan
 *  y .
 *
 * @param <T>  tipo de la entidad JPA
 * @param <ID> tipo de la llave primaria; debe ser Serializable
 *
 */
public abstract class Model<T, ID extends Serializable> implements Serializable {

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
            LOGGER.log(Level.SEVERE, "Error al cargar datos", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los datos.");
        }
    }

    /** Prepara un registro nuevo y cambia el estado a . */
    public void prepararNuevo() {
        registroActual = crearNuevoRegistro();
        estado = Estado.CREAR;
    }

    /**
     * Selecciona un registro existente para edición.
     * @param registro el registro seleccionado por el usuario
     */
    public void seleccionar(T registro) {
        registroActual = registro;
        estado = Estado.MODIFICAR;
    }

    /** Cancela la operación en curso y restablece el estado a . */
    public void cancelar() {
        registroActual = null;
        estado = Estado.NINGUNO;
    }

    /**
     * Persiste o actualiza el registro actual según el .
     * Recarga datos y cancela la operación al finalizar exitosamente.
     */
    public void guardar() {
        try {
            if (estado == Estado.CREAR) {
                getDAO().create(registroActual);
                agregarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Registro creado correctamente.");
            } else if (estado == Estado.MODIFICAR) {
                getDAO().update(registroActual);
                agregarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Registro actualizado correctamente.");
            }
            cargarDatos();
            cancelar(); 
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar registro", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Error", clasificarError(e));
        }
    }

    /**
     * Elimina un registro de la base de datos y recarga la lista
     * @param registro el registro a eliminar
     */
    public void eliminar(T registro) {
        try {
            estado = Estado.ELIMINAR;
            getDAO().delete(registro);
            cargarDatos();
            if (registroActual != null && registro.equals(registroActual)) {
                cancelar();
            }
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Registro eliminado correctamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar registro", e);
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
        Throwable causa = e;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        String msg = causa.getMessage();
        if (msg == null) {
            return "Error inesperado. Contacte al administrador del sistema.";
        }

        String msgLower = msg.toLowerCase();

        // ── Violación de restricción UNIQUE ──
        if (msgLower.contains("unique") || msgLower.contains("duplicate key")
                || msgLower.contains("llave duplicada") || msgLower.contains("duplicat")) {
            return "Ya existe un registro con estos datos. Verifique los campos que deben ser únicos.";
        }

        // ── Violación de FK / integridad referencial ──
        if (msgLower.contains("foreign key") || msgLower.contains("fk_")
                || msgLower.contains("referential integrity") || msgLower.contains("llave foránea")
                || msgLower.contains("is still referenced")) {
            return "No se puede completar la operación: este registro está relacionado con otros datos.";
        }

        // ── NOT NULL ──
        if (msgLower.contains("not-null") || msgLower.contains("not null")
                || msgLower.contains("violates not-null") || msgLower.contains("null value")) {
            return "Faltan campos obligatorios. Complete todos los datos requeridos.";
        }

        // ── Texto demasiado largo ──
        if (msgLower.contains("value too long") || msgLower.contains("character varying")) {
            return "Uno de los campos excede la longitud máxima permitida.";
        }

        // ── Genérico con detalle (truncado a 200 chars) ──
        return "Error al procesar: " + (msg.length() > 200 ? msg.substring(0, 200) + "…" : msg);
    }

    // ─── Mensajes JSF ────────────────────────────────────────────────

    /**
     * Agrega un mensaje global a la cola de mensajes de JSF.
     *
     * @param severidad nivel de severidad del mensaje
     * @param titulo    resumen corto
     * @param detalle   descripción detallada
     */
    protected void agregarMensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                facesContext.addMessage(null, new FacesMessage(severidad, titulo, detalle));
            }
        } catch (Throwable t) {
            LOGGER.log(Level.FINE, "FacesContext no disponible para mensaje: {0} - {1}", new Object[]{titulo, detalle});
        }
    }

    // ─── Estado ──────────────────────────────────────────────────────

    /** @return true si el estado actual es  */
    public boolean isEstadoCrear() { return estado == Estado.CREAR; }

    /** @return true si el estado actual es  */
    public boolean isEstadoModificar() { return estado == Estado.MODIFICAR; }

    /** @return true si el estado actual es  */
    public boolean isEstadoEliminar() { return estado == Estado.ELIMINAR; }

    /** @return true si el estado actual es  */
    public boolean isEstadoNinguno() { return estado == Estado.NINGUNO; }

    // ─── Accessors ───────────────────────────────────────────────────

    public List<T> getRegistros() { return registros; }
    public void setRegistros(List<T> registros) { this.registros = registros; }
    public T getRegistroActual() { return registroActual; }
    public void setRegistroActual(T registroActual) { this.registroActual = registroActual; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
