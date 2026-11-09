package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

/**
 * Servicio de negocio para la gestión de asignaciones entre exámenes y tipos de examen.
 */
@ApplicationScoped
public class ExamenTipoExamenService extends DefaultService<ExamenTipoExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ExamenTipoExamenDAO examenTipoExamenDAO;

    @Inject
    protected AsignacionService asignacionService;

    public ExamenTipoExamenService() {
    }

    public ExamenTipoExamenService(ExamenTipoExamenDAO examenTipoExamenDAO, AsignacionService asignacionService) {
        this.examenTipoExamenDAO = examenTipoExamenDAO;
        this.asignacionService = asignacionService;
    }

    @Override
    protected DefaultDAO<ExamenTipoExamen, UUID> getDAO() {
        return examenTipoExamenDAO;
    }

    @Override
    protected void validar(ExamenTipoExamen r, boolean nuevo) {
        if (nuevo && r.getFechaCreacion() == null) {
            r.setFechaCreacion(new Date());
        }
    }

    @Override
    @Transactional
    public void crear(ExamenTipoExamen e) {
        validar(e, true);
        asignacionService.guardarTipo(e, true);
    }

    @Override
    @Transactional
    public void actualizar(ExamenTipoExamen e) {
        validar(e, false);
        asignacionService.guardarTipo(e, false);
    }

    public List<ExamenTipoExamen> findByExamen(UUID idExamen) {
        return examenTipoExamenDAO.findByExamen(idExamen);
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
