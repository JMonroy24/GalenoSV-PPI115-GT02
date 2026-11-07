package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

@ApplicationScoped
public class ProcedimientoPasoSecuenciaService extends DefaultService<ProcedimientoPasoSecuencia, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO;

    @Inject
    protected AsignacionService asignacionService;

    public ProcedimientoPasoSecuenciaService() {
    }

    public ProcedimientoPasoSecuenciaService(ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO,
            AsignacionService asignacionService) {
        this.procedimientoPasoSecuenciaDAO = procedimientoPasoSecuenciaDAO;
        this.asignacionService = asignacionService;
    }

    @Override
    protected DefaultDAO<ProcedimientoPasoSecuencia, UUID> getDAO() {
        return procedimientoPasoSecuenciaDAO;
    }

    @Override
    @Transactional
    public void crear(ProcedimientoPasoSecuencia e) {
        asignacionService.guardarSecuencia(e, true);
    }

    @Override
    @Transactional
    public void actualizar(ProcedimientoPasoSecuencia e) {
        asignacionService.guardarSecuencia(e, false);
    }

    public List<ProcedimientoPasoSecuencia> findByPaso(UUID idPaso) {
        if (idPaso == null) {
            return List.of();
        }
        return procedimientoPasoSecuenciaDAO.findByPaso(idPaso);
    }

    public List<ProcedimientoPasoSecuencia> findByProcedimiento(UUID idProcedimiento) {
        if (idProcedimiento == null) {
            return List.of();
        }
        return procedimientoPasoSecuenciaDAO.findByProcedimiento(idProcedimiento);
    }

    public boolean existeSecuencia(UUID idPaso, UUID idReferencia, String tipo) {
        return procedimientoPasoSecuenciaDAO.existeSecuencia(idPaso, idReferencia, tipo);
    }

    public boolean existeSecuencia(UUID idPaso, UUID idReferencia, String tipo, UUID excluirId) {
        return procedimientoPasoSecuenciaDAO.existeSecuencia(idPaso, idReferencia, tipo, excluirId);
    }

    public ProcedimientoPasoSecuenciaDAO getProcedimientoPasoSecuenciaDAO() {
        return procedimientoPasoSecuenciaDAO;
    }

    public void setProcedimientoPasoSecuenciaDAO(ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO) {
        this.procedimientoPasoSecuenciaDAO = procedimientoPasoSecuenciaDAO;
    }

    public AsignacionService getAsignacionService() {
        return asignacionService;
    }

    public void setAsignacionService(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }
}
