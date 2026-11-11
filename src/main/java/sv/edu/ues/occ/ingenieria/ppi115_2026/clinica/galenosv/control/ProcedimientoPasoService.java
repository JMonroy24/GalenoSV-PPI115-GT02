package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

@ApplicationScoped
public class ProcedimientoPasoService extends DefaultService<ProcedimientoPaso, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoPasoDAO procedimientoPasoDAO;

    @Inject
    protected AsignacionService asignacionService;

    public ProcedimientoPasoService() {
    }

    public ProcedimientoPasoService(ProcedimientoPasoDAO procedimientoPasoDAO) {
        this.procedimientoPasoDAO = procedimientoPasoDAO;
    }

    public ProcedimientoPasoService(ProcedimientoPasoDAO procedimientoPasoDAO,
            AsignacionService asignacionService) {
        this.procedimientoPasoDAO = procedimientoPasoDAO;
        this.asignacionService = asignacionService;
    }

    @Override
    protected DefaultDAO<ProcedimientoPaso, UUID> getDAO() {
        return procedimientoPasoDAO;
    }

    @Override
    protected void validar(ProcedimientoPaso r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre del paso"));
    }

    public List<ProcedimientoPaso> findByProcedimiento(UUID idProcedimiento) {
        if (idProcedimiento == null) {
            return List.of();
        }
        return procedimientoPasoDAO.findByProcedimiento(idProcedimiento);
    }

    public boolean tieneFin(UUID idProcedimiento) {
        return procedimientoPasoDAO.tieneFin(idProcedimiento);
    }

    public ProcedimientoPaso findPasoInicial(UUID idProcedimiento) {
        return procedimientoPasoDAO.findPasoInicial(idProcedimiento);
    }

    public ProcedimientoPasoDAO getProcedimientoPasoDAO() {
        return procedimientoPasoDAO;
    }

    public void setProcedimientoPasoDAO(ProcedimientoPasoDAO procedimientoPasoDAO) {
        this.procedimientoPasoDAO = procedimientoPasoDAO;
    }

    @Override
    @Transactional
    public void eliminar(ProcedimientoPaso e) {
        asignacionService.eliminarPaso(e);
    }

    public AsignacionService getAsignacionService() {
        return asignacionService;
    }

    public void setAsignacionService(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }
}
