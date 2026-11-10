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

    public ProcedimientoPasoService(ProcedimientoPasoDAO procedimientoPasoDAO, AsignacionService asignacionService) {
        this.procedimientoPasoDAO = procedimientoPasoDAO;
        this.asignacionService = asignacionService;
    }

    @Override
    protected DefaultDAO<ProcedimientoPaso, UUID> getDAO() {
        return procedimientoPasoDAO;
    }

    @Override
    @Transactional
    public void crear(ProcedimientoPaso paso) {
        asignacionService.guardarPaso(paso, true, null);
    }

    @Override
    @Transactional
    public void actualizar(ProcedimientoPaso paso) {
        asignacionService.guardarPaso(paso, false, null);
    }

    @Override
    @Transactional
    public void eliminar(ProcedimientoPaso paso) {
        asignacionService.eliminarPaso(paso);
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
}
