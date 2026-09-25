package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenTipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

/**
 * Administra el catálogo de exámenes y sus asociaciones con tipos de examen.
 * Cada asociación se guarda mediante ExamenTipoExamenDAO.
 */
@Named("examenModel")
@ViewScoped
public class ExamenModel extends ModelTransaccional<Examen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ExamenModel.class.getName());

    @Inject
    protected ExamenDAO examenDAO;

    @Inject
    protected ExamenTipoExamenDAO examenTipoExamenDAO;

    private List<ExamenTipoExamen> tiposAsignados = Collections.emptyList();
    private TipoExamen tipoSeleccionado;
    private String observacionesTipo;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<Examen, UUID> getDAO() {
        return examenDAO;
    }

    /**
     * Crea un examen con el identificador requerido para guardarlo.
     *
     * @return examen nuevo con UUID asignado
     */
    @Override
    protected Examen crearNuevoRegistro() {
        Examen examen = new Examen();
        examen.setIdExamen(UUID.randomUUID());
        return examen;
    }

    /**
     * Carga los tipos asociados al examen seleccionado para edición.
     *
     * @param examen examen seleccionado en el catálogo
     */
    @Override
    public void seleccionar(Examen examen) {
        super.seleccionar(examen);
        tiposAsignados = examenTipoExamenDAO.findByExamen(examen.getIdExamen());
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

    /**
     * Asocia el tipo elegido con un examen guardado. Impide que el mismo
     * tipo se asigne dos veces al examen.
     */
    public void agregarTipo() {
        if (!isEstadoModificar() || getRegistroActual() == null
                || tipoSeleccionado == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    "Seleccione un tipo para un examen guardado.");
            return;
        }

        UUID idTipo = tipoSeleccionado.getIdTipoExamen();
        boolean yaAsignado = tiposAsignados.stream()
                .anyMatch(asignacion -> asignacion.getIdTipoExamen() != null
                && idTipo.equals(
                        asignacion.getIdTipoExamen().getIdTipoExamen()));

        if (yaAsignado) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    "Este tipo ya está asignado al examen.");
            return;
        }

        ExamenTipoExamen asignacion = new ExamenTipoExamen(UUID.randomUUID());
        asignacion.setIdExamen(getRegistroActual());
        asignacion.setIdTipoExamen(tipoSeleccionado);
        asignacion.setFechaCreacion(new Date());
        asignacion.setObservaciones(observacionesTipo);

        try {
            examenTipoExamenDAO.create(asignacion);
            tiposAsignados = examenTipoExamenDAO.findByExamen(
                    getRegistroActual().getIdExamen());
            limpiarSeleccionTipo();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Tipo de examen",
                    "Tipo asignado correctamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al asignar un tipo al examen", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    clasificarError(e));
        }
    }

    /**
     * Quita una asociación perteneciente al examen seleccionado.
     *
     * @param asignacion asociación que se quitará
     */
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
            examenTipoExamenDAO.delete(asignacion);
            tiposAsignados = examenTipoExamenDAO.findByExamen(
                    getRegistroActual().getIdExamen());
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Tipo de examen",
                    "Tipo quitado correctamente.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al quitar un tipo del examen", e);
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Tipo de examen",
                    clasificarError(e));
        }
    }

    private void limpiarSeleccionTipo() {
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

    public ExamenDAO getExamenDAO() {
        return examenDAO;
    }

    public void setExamenDAO(ExamenDAO examenDAO) {
        this.examenDAO = examenDAO;
    }
}