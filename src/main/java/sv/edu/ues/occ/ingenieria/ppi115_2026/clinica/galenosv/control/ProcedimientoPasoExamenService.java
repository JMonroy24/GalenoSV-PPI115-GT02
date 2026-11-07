package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

@ApplicationScoped
public class ProcedimientoPasoExamenService extends DefaultService<ProcedimientoPasoExamen, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;

    @Inject
    protected AsignacionService asignacionService;

    public ProcedimientoPasoExamenService() {
    }

    public ProcedimientoPasoExamenService(ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO,
            AsignacionService asignacionService) {
        this.procedimientoPasoExamenDAO = procedimientoPasoExamenDAO;
        this.asignacionService = asignacionService;
    }

    @Override
    protected DefaultDAO<ProcedimientoPasoExamen, UUID> getDAO() {
        return procedimientoPasoExamenDAO;
    }

    @Override
    @Transactional
    public void crear(ProcedimientoPasoExamen e) {
        asignacionService.guardarExamen(e, true);
    }

    @Override
    @Transactional
    public void actualizar(ProcedimientoPasoExamen e) {
        asignacionService.guardarExamen(e, false);
    }

    public List<ProcedimientoPasoExamen> findByPaso(UUID idPaso) {
        if (idPaso == null) {
            return List.of();
        }
        return procedimientoPasoExamenDAO.findByPaso(idPaso);
    }

    public boolean existePasoExamen(UUID idPaso, UUID idExamen) {
        return procedimientoPasoExamenDAO.existePasoExamen(idPaso, idExamen);
    }

    public boolean existePasoExamen(UUID idPaso, UUID idExamen, UUID excluirId) {
        return procedimientoPasoExamenDAO.existePasoExamen(idPaso, idExamen, excluirId);
    }

    public ProcedimientoPasoExamenDAO getProcedimientoPasoExamenDAO() {
        return procedimientoPasoExamenDAO;
    }

    public void setProcedimientoPasoExamenDAO(ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO) {
        this.procedimientoPasoExamenDAO = procedimientoPasoExamenDAO;
    }

    public AsignacionService getAsignacionService() {
        return asignacionService;
    }

    public void setAsignacionService(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }
}
