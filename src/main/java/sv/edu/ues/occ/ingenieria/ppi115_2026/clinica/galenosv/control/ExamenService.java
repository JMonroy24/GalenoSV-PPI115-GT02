package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

/**
 * Servicio de negocio para la gestión de exámenes clínicos y sus tipos asignados.
 */
@ApplicationScoped
public class ExamenService extends DefaultService<Examen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ExamenDAO examenDAO;

    @Inject
    protected ExamenTipoExamenDAO examenTipoExamenDAO;

    @Inject
    protected AsignacionService asignacionService;

    public ExamenService() {
    }

    public ExamenService(ExamenDAO examenDAO, ExamenTipoExamenDAO examenTipoExamenDAO, AsignacionService asignacionService) {
        this.examenDAO = examenDAO;
        this.examenTipoExamenDAO = examenTipoExamenDAO;
        this.asignacionService = asignacionService;
    }

    @Override
    protected DefaultDAO<Examen, UUID> getDAO() {
        return examenDAO;
    }

    @Override
    protected void validar(Examen r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre"));
        if (examenDAO.existePorCampo("nombre", r.getNombre(), r.getIdExamen())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public List<Examen> getActivos() {
        return findAllActivos();
    }

    public List<Examen> findAllActivos() {
        return examenDAO.findAllActivos();
    }

    public List<ExamenTipoExamen> findTiposByExamen(UUID idExamen) {
        if (idExamen == null) return List.of();
        return examenTipoExamenDAO.findByExamen(idExamen);
    }

    @Transactional
    public void agregarTipo(ExamenTipoExamen asignacion) {
        asignacionService.guardarTipo(asignacion, true);
    }

    @Transactional
    public void quitarTipo(ExamenTipoExamen asignacion) {
        if (asignacion != null) {
            examenTipoExamenDAO.delete(asignacion);
        }
    }

    @Transactional
    public void crearExamenConTipo(Examen examen, ExamenTipoExamen asignacion) {
        validar(examen, true);
        asignacionService.crearExamenConTipo(examen, asignacion);
    }

    public ExamenDAO getExamenDAO() {
        return examenDAO;
    }

    public void setExamenDAO(ExamenDAO examenDAO) {
        this.examenDAO = examenDAO;
    }

    public ExamenTipoExamenDAO getExamenTipoExamenDAO() {
        return examenTipoExamenDAO;
    }

    public void setExamenTipoExamenDAO(ExamenTipoExamenDAO examenTipoExamenDAO) {
        this.examenTipoExamenDAO = examenTipoExamenDAO;
    }

    public AsignacionService getAsignacionService() {
        return asignacionService;
    }

    public void setAsignacionService(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }
}
