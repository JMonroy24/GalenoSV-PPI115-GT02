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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultDAO;
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

    @Inject
    protected ExamenService examenService;

    private List<ExamenTipoExamen> tiposAsignados = Collections.emptyList();
    private TipoExamen tipoSeleccionado;
    private ExamenTipoExamen asignacionTipoSeleccionada;
    private String observacionesTipo;
    private String filtroTipos;

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
        try {
            tiposAsignados = examenService.findTiposByExamen(examen.getIdExamen());
            super.seleccionar(examen);
            limpiarSeleccionTipo();
        } catch (RuntimeException e) {
            notificarError("cargar tipos del examen", e);
        }
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
            notificarError("asignar un tipo al examen", e);
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
            notificarError("quitar un tipo del examen", e);
        }
    }

    private void limpiarSeleccionTipo() {
        setFiltroTipos(null);
        tipoSeleccionado = null;
        observacionesTipo = null;
    }

    public List<ExamenTipoExamen> getTiposAsignados() {
        return tiposAsignados;
    }

    public List<ExamenTipoExamen> getTiposVisibles() {
        return filtrarPorNombre(tiposAsignados, filtroTipos,
                asignacion -> asignacion.getIdTipoExamen() == null ? null : asignacion.getIdTipoExamen().getNombre());
    }

    public String getFiltroTipos() { return filtroTipos; }

    public void setFiltroTipos(String filtro) {
        filtroTipos = DefaultDAO.normalizarFiltro(filtro);
        asignacionTipoSeleccionada = null;
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
        if (!isEstadoCrear()) return;
        if (tipoSeleccionado != null) {
            ValidadorComun.activo(tipoSeleccionado.getActivo(), "El tipo de examen");
        } else if (observacionesTipo != null && !observacionesTipo.isBlank()) {
            throw new ValidacionNegocioException("Seleccione un tipo para guardar sus observaciones.");
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

    public List<Examen> completeExamen(String query) {
        return completar(query, 1, texto -> examenService.buscarParaAutocompletar(texto, 20));
    }
}
