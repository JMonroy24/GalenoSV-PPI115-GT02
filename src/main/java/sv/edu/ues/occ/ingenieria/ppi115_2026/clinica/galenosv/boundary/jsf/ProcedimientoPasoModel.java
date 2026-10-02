package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.AsignacionService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

/**
 * Administra los pasos de procedimientos, sus exámenes asociados y las
 * relaciones de secuencia entre pasos del mismo procedimiento.
 */
@Named("procedimientoPasoModel")
@ViewScoped
public class ProcedimientoPasoModel extends ModelTransaccional<ProcedimientoPaso, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER
            = Logger.getLogger(ProcedimientoPasoModel.class.getName());

    @Inject
    protected ProcedimientoPasoDAO procedimientoPasoDAO;

    @Inject
    protected ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;

    @Inject
    protected ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO;

    private List<ProcedimientoPasoExamen> examenesAsignados
            = Collections.emptyList();
    private List<ProcedimientoPasoSecuencia> secuencias
            = Collections.emptyList();

    private Examen examenSeleccionado;
    private String observacionesExamen;
    private boolean examenActivo = true;

    private ProcedimientoPaso pasoReferenciaSeleccionado;
    private String tipoSecuencia;

    @Inject
    protected AsignacionService asignacionService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ProcedimientoPaso, UUID> getDAO() {
        return procedimientoPasoDAO;
    }

    /**
     * Prepara un paso nuevo con su identificador UUID.
     *
     * @return paso nuevo listo para completar
     */
    @Override
    protected ProcedimientoPaso crearNuevoRegistro() {
        return new ProcedimientoPaso(UUID.randomUUID());
    }

    /**
     * Carga las dos clases de asociaciones del paso elegido.
     *
     * @param paso paso seleccionado en el catálogo
     */
    @Override
    public void seleccionar(ProcedimientoPaso paso) {
        if (paso == null) return;
        super.seleccionar(paso);
        UUID idPaso = paso.getIdProcedimientoPaso();
        examenesAsignados = procedimientoPasoExamenDAO.findByPaso(idPaso);
        secuencias = procedimientoPasoSecuenciaDAO.findByPaso(idPaso);
        limpiarCamposRelaciones();
    }

    @Override
    public void prepararNuevo() {
        super.prepararNuevo();
        examenesAsignados = Collections.emptyList();
        secuencias = Collections.emptyList();
        limpiarCamposRelaciones();
    }

    @Override
    public void cancelar() {
        super.cancelar();
        examenesAsignados = Collections.emptyList();
        secuencias = Collections.emptyList();
        limpiarCamposRelaciones();
    }

    /**
     * Asigna un examen al paso guardado, conservando estado y observaciones.
     */
    public void agregarExamen() {
        if (!isEstadoModificar() || getRegistroActual() == null
                || examenSeleccionado == null || examenSeleccionado.getIdExamen() == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Examen del paso",
                    "Seleccione un examen para un paso guardado.");
            return;
        }

        UUID idExamen = examenSeleccionado.getIdExamen();
        boolean yaAsignado = examenesAsignados.stream()
                .anyMatch(asignacion -> asignacion.getIdExamen() != null
                && idExamen.equals(asignacion.getIdExamen().getIdExamen()));

        if (yaAsignado) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Examen del paso",
                    "Este examen ya está asignado al paso.");
            return;
        }

        ProcedimientoPasoExamen asignacion
                = new ProcedimientoPasoExamen(UUID.randomUUID());
        asignacion.setIdProcedimientoPaso(getRegistroActual());
        asignacion.setIdExamen(examenSeleccionado);
        asignacion.setFechaCreacion(new Date());
        asignacion.setActivo(examenActivo);
        asignacion.setObservaciones(observacionesExamen);

        try {
            asignacionService.guardarExamen(asignacion, true);
            examenesAsignados = procedimientoPasoExamenDAO.findByPaso(
                    getRegistroActual().getIdProcedimientoPaso());
            limpiarCamposExamen();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Examen del paso",
                    "Examen asignado correctamente.");
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage());
            marcarValidacionFallida();
        } catch (Exception e) {
            marcarValidacionFallida();
            LOGGER.log(Level.SEVERE, "Error al asignar examen al paso", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Examen del paso",
                    clasificarError(e));
        }
    }

    /**
     * Quita una asociación de examen perteneciente al paso seleccionado.
     *
     * @param asignacion asociación que se quitará
     */
    public void quitarExamen(ProcedimientoPasoExamen asignacion) {
        if (!isEstadoModificar() || getRegistroActual() == null
                || asignacion == null
                || examenesAsignados.stream().noneMatch(actual
                        -> Objects.equals(
                        actual.getIdProcedimientoPasoExamen(),
                        asignacion.getIdProcedimientoPasoExamen()))) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Examen del paso",
                    "Seleccione un examen asignado a este paso.");
            return;
        }

        try {
            procedimientoPasoExamenDAO.delete(asignacion);
            examenesAsignados = procedimientoPasoExamenDAO.findByPaso(
                    getRegistroActual().getIdProcedimientoPaso());
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Examen del paso",
                    "Examen quitado correctamente.");
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage());
            marcarValidacionFallida();
        } catch (Exception e) {
            marcarValidacionFallida();
            LOGGER.log(Level.SEVERE, "Error al quitar examen del paso", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Examen del paso",
                    clasificarError(e));
        }
    }

    /**
     * Devuelve los demás pasos del mismo procedimiento para elegir una
     * referencia válida de secuencia. Consulta el DAO directamente porque en
     * modelos transaccionales getRegistros() siempre es null.
     *
     * @return pasos del mismo procedimiento, sin el paso actual
     */
    public List<ProcedimientoPaso> getPasosReferenciaDisponibles() {
        if (getRegistroActual() == null
                || getRegistroActual().getIdProcedimiento() == null) {
            return Collections.emptyList();
        }

        UUID idProcedimiento = getRegistroActual()
                .getIdProcedimiento().getIdProcedimiento();
        UUID idPasoActual = getRegistroActual().getIdProcedimientoPaso();

        return procedimientoPasoDAO.findByProcedimiento(idProcedimiento, idPasoActual);
    }

    /**
     * Obtiene el nombre de un paso referenciado por su UUID.
     *
     * @param idReferencia UUID guardado en la relación de secuencia
     * @return nombre del paso, o su UUID si ya no está en el catálogo
     */
    public String nombrePasoReferencia(UUID idReferencia) {
        if (idReferencia == null) {
            return "";
        }
        ProcedimientoPaso paso = procedimientoPasoDAO.findById(idReferencia);
        return paso == null || paso.getNombre() == null ? idReferencia.toString() : paso.getNombre();
    }

    /**
     * Relaciona el paso actual con otro paso del mismo procedimiento.
     */
    public void agregarSecuencia() {
        if (!isEstadoModificar() || getRegistroActual() == null
                || pasoReferenciaSeleccionado == null
                || tipoSecuencia == null || tipoSecuencia.isBlank()) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Secuencia",
                    "Seleccione un paso de referencia e indique el tipo de secuencia.");
            return;
        }

        String tipo = tipoSecuencia.trim();
        if (tipo.length() > 20) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Secuencia",
                    "El tipo de secuencia no puede superar 20 caracteres.");
            return;
        }

        UUID idReferencia
                = pasoReferenciaSeleccionado.getIdProcedimientoPaso();
        boolean referenciaValida = getPasosReferenciaDisponibles().stream()
                .anyMatch(paso -> Objects.equals(
                paso.getIdProcedimientoPaso(), idReferencia));

        if (!referenciaValida) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Secuencia",
                    "El paso de referencia debe pertenecer al mismo procedimiento.");
            return;
        }

        boolean yaExiste = secuencias.stream()
                .anyMatch(secuencia -> Objects.equals(
                secuencia.getIdProcedimientoPasoReferencia(),
                idReferencia)
                && tipo.equalsIgnoreCase(secuencia.getTipoSecuencia()));

        if (yaExiste) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Secuencia",
                    "Esta relación de secuencia ya existe.");
            return;
        }

        ProcedimientoPasoSecuencia secuencia
                = new ProcedimientoPasoSecuencia(UUID.randomUUID());
        secuencia.setIdProcedimientoPaso(getRegistroActual());
        secuencia.setIdProcedimientoPasoReferencia(idReferencia);
        secuencia.setTipoSecuencia(tipo);

        try {
            asignacionService.guardarSecuencia(secuencia, true);
            secuencias = procedimientoPasoSecuenciaDAO.findByPaso(
                    getRegistroActual().getIdProcedimientoPaso());
            limpiarCamposSecuencia();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Secuencia",
                    "Secuencia agregada correctamente.");
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage());
            marcarValidacionFallida();
        } catch (Exception e) {
            marcarValidacionFallida();
            LOGGER.log(Level.SEVERE, "Error al agregar secuencia", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Secuencia",
                    clasificarError(e));
        }
    }

    /**
     * Quita una relación de secuencia del paso seleccionado.
     *
     * @param secuencia relación que se quitará
     */
    public void quitarSecuencia(ProcedimientoPasoSecuencia secuencia) {
        if (!isEstadoModificar() || getRegistroActual() == null
                || secuencia == null
                || secuencias.stream().noneMatch(actual
                        -> Objects.equals(
                        actual.getIdProcedimientoPasoSecuencia(),
                        secuencia.getIdProcedimientoPasoSecuencia()))) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Secuencia",
                    "Seleccione una secuencia perteneciente al paso.");
            return;
        }

        try {
            procedimientoPasoSecuenciaDAO.delete(secuencia);
            secuencias = procedimientoPasoSecuenciaDAO.findByPaso(
                    getRegistroActual().getIdProcedimientoPaso());
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Secuencia",
                    "Secuencia quitada correctamente.");
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage());
            marcarValidacionFallida();
        } catch (Exception e) {
            marcarValidacionFallida();
            LOGGER.log(Level.SEVERE, "Error al quitar secuencia", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Secuencia",
                    clasificarError(e));
        }
    }

    private void limpiarCamposExamen() {
        examenSeleccionado = null;
        observacionesExamen = null;
        examenActivo = true;
    }

    private void limpiarCamposSecuencia() {
    pasoReferenciaSeleccionado = null;
    tipoSecuencia = "SIGUIENTE";
        }

    private void limpiarCamposRelaciones() {
        limpiarCamposExamen();
        limpiarCamposSecuencia();
    }

    public List<ProcedimientoPasoExamen> getExamenesAsignados() {
        return examenesAsignados;
    }

    public List<ProcedimientoPasoSecuencia> getSecuencias() {
        return secuencias;
    }

    public Examen getExamenSeleccionado() {
        return examenSeleccionado;
    }

    public void setExamenSeleccionado(Examen examenSeleccionado) {
        this.examenSeleccionado = examenSeleccionado;
    }

    public String getObservacionesExamen() {
        return observacionesExamen;
    }

    public void setObservacionesExamen(String observacionesExamen) {
        this.observacionesExamen = observacionesExamen;
    }

    public boolean isExamenActivo() {
        return examenActivo;
    }

    public void setExamenActivo(boolean examenActivo) {
        this.examenActivo = examenActivo;
    }

    public ProcedimientoPaso getPasoReferenciaSeleccionado() {
        return pasoReferenciaSeleccionado;
    }

    public void setPasoReferenciaSeleccionado(
            ProcedimientoPaso pasoReferenciaSeleccionado) {
        this.pasoReferenciaSeleccionado = pasoReferenciaSeleccionado;
    }

    public String getTipoSecuencia() {
        return tipoSecuencia;
    }

    public void setTipoSecuencia(String tipoSecuencia) {
        this.tipoSecuencia = tipoSecuencia;
    }

    public ProcedimientoPasoDAO getProcedimientoPasoDAO() {
        return procedimientoPasoDAO;
    }

    public void setProcedimientoPasoDAO(
            ProcedimientoPasoDAO procedimientoPasoDAO) {
        this.procedimientoPasoDAO = procedimientoPasoDAO;
    }
    
    

    @Override
    protected void validarNegocio(ProcedimientoPaso registro) {
        registro.setNombre(ValidadorComun.textoObligatorio(registro.getNombre(), "El nombre del paso"));
        var procedimiento = ValidadorComun.requerido(registro.getIdProcedimiento(), "Seleccione un procedimiento.");
        ValidadorComun.requerido(procedimiento.getIdProcedimiento(), "Seleccione un procedimiento válido.");
        if (procedimientoPasoDAO.existeNombreEnProcedimiento(procedimiento.getIdProcedimiento(),
                registro.getNombre(), registro.getIdProcedimientoPaso())) {
            throw new ValidacionNegocioException("Ya existe un paso con este nombre en el procedimiento.");
        }
    }

}
