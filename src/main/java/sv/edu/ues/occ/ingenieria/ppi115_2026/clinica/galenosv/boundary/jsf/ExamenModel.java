package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

/**
 * Administra el catálogo de exámenes y sus asociaciones con tipos de examen.
 */
@Named("examenModel")
@ViewScoped
public class ExamenModel extends ModelTransaccional<Examen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ExamenModel.class.getName());

    @Inject
    protected ExamenService examenService;

    private List<ExamenTipoExamen> tiposAsignados = Collections.emptyList();
    private TipoExamen tipoSeleccionado;
    private ExamenTipoExamen asignacionTipoSeleccionada;
    private String observacionesTipo;

    public ExamenTipoExamen getAsignacionTipoSeleccionada() { return asignacionTipoSeleccionada; }
    public void setAsignacionTipoSeleccionada(ExamenTipoExamen seleccionada) { asignacionTipoSeleccionada = seleccionada; }
    public void quitarTipoSeleccionado() {
        quitarTipo(asignacionTipoSeleccionada);
        asignacionTipoSeleccionada = null;
    }

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<Examen, UUID> getDAO() {
        return examenService;
    }

    @Override
    protected Examen crearNuevoRegistro() {
        Examen examen = new Examen();
        examen.setIdExamen(UUID.randomUUID());
        examen.setActivo(true);
        return examen;
    }

    @Override
    public void seleccionar(Examen examen) {
        if (examen == null) return;
        super.seleccionar(examen);
        tiposAsignados = examenService.findTiposByExamen(examen.getIdExamen());
        limpiarSeleccionTipo();
    }

    @Override
    public void prepararNuevo() {
        super.prepararNuevo();
        tiposAsignados = Collections.emptyList();
        limpiarSeleccionTipo();
    }

    @Override
    public void cancelar() {
        super.cancelar();
        tiposAsignados = Collections.emptyList();
        limpiarSeleccionTipo();
    }

    public void agregarTipo() {
        if (!isEstadoModificar() || getRegistroActual() == null
                || tipoSeleccionado == null || tipoSeleccionado.getIdTipoExamen() == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    "Seleccione un tipo para un examen guardado.");
            return;
        }

        if (!Boolean.TRUE.equals(tipoSeleccionado.getActivo())) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen", "No se puede asignar un tipo de examen inactivo.");
            return;
        }
        ExamenTipoExamen asignacion = new ExamenTipoExamen(UUID.randomUUID());
        asignacion.setIdExamen(getRegistroActual());
        asignacion.setIdTipoExamen(tipoSeleccionado);
        asignacion.setFechaCreacion(new Date());
        asignacion.setObservaciones(observacionesTipo);

        try {
            examenService.agregarTipo(asignacion);
            tiposAsignados = examenService.findTiposByExamen(getRegistroActual().getIdExamen());
            limpiarSeleccionTipo();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Tipo de examen",
                    "Tipo asignado correctamente.");
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage());
            marcarValidacionFallida();
        } catch (Exception e) {
            marcarValidacionFallida();
            LOGGER.log(Level.SEVERE, "Error al asignar un tipo al examen", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    clasificarError(e));
        }
    }

    public void quitarTipo(ExamenTipoExamen asignacion) {
        if (!isEstadoModificar() || getRegistroActual() == null
                || asignacion == null
                || tiposAsignados.stream().noneMatch(actual ->
                        actual.getIdExamenTipoExamen().equals(
                                asignacion.getIdExamenTipoExamen()))) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    "Seleccione una asociación válida del examen.");
            return;
        }

        try {
            examenService.quitarTipo(asignacion);
            tiposAsignados = examenService.findTiposByExamen(getRegistroActual().getIdExamen());
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Tipo de examen",
                    "Tipo quitado correctamente.");
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Validación", e.getMessage());
            marcarValidacionFallida();
        } catch (Exception e) {
            marcarValidacionFallida();
            LOGGER.log(Level.SEVERE, "Error al quitar un tipo del examen", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    clasificarError(e));
        }
    }

    private void limpiarSeleccionTipo() {
        asignacionTipoSeleccionada = null;
        tipoSeleccionado = null;
        observacionesTipo = null;
    }

    public List<ExamenTipoExamen> getTiposAsignados() {
        return tiposAsignados;
    }

    public TipoExamen getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public void setTipoSeleccionado(TipoExamen tipoSeleccionado) {
        this.tipoSeleccionado = tipoSeleccionado;
    }

    public String getObservacionesTipo() {
        return observacionesTipo;
    }

    public void setObservacionesTipo(String observacionesTipo) {
        this.observacionesTipo = observacionesTipo;
    }

    public ExamenService getExamenService() {
        return examenService;
    }

    public void setExamenService(ExamenService examenService) {
        this.examenService = examenService;
    }

    public java.util.List<Examen> getExamenesActivos() {
        return examenService.getActivos();
    }

    @Override
    protected void validarNegocio(Examen registro) {
        if (isEstadoCrear() && tipoSeleccionado != null) {
            ValidadorComun.activo(tipoSeleccionado.getActivo(), "El tipo de examen");
        }
    }

    @Override
    protected void persistirNuevo(Examen examen) {
        if (tipoSeleccionado == null) {
            super.persistirNuevo(examen);
            return;
        }
        ExamenTipoExamen asignacion = new ExamenTipoExamen(UUID.randomUUID());
        asignacion.setIdExamen(examen);
        asignacion.setIdTipoExamen(tipoSeleccionado);
        asignacion.setFechaCreacion(new Date());
        asignacion.setObservaciones(observacionesTipo);
        examenService.crearExamenConTipo(examen, asignacion);
    }
}
