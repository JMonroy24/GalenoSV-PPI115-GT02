package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

/**
 * Servicio de negocio para la gestión de procedimientos clínicos y sus árboles de pasos.
 */
@ApplicationScoped
public class ProcedimientoService extends DefaultService<Procedimiento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoDAO procedimientoDAO;

    @Inject
    protected ProcedimientoPasoDAO pasoDAO;

    @Inject
    protected ProcedimientoPasoSecuenciaDAO secuenciaDAO;

    public ProcedimientoService() {
    }

    public ProcedimientoService(ProcedimientoDAO procedimientoDAO, ProcedimientoPasoDAO pasoDAO,
            ProcedimientoPasoSecuenciaDAO secuenciaDAO) {
        this.procedimientoDAO = procedimientoDAO;
        this.pasoDAO = pasoDAO;
        this.secuenciaDAO = secuenciaDAO;
    }

    @Override
    protected DefaultDAO<Procedimiento, UUID> getDAO() {
        return procedimientoDAO;
    }

    @Override
    protected void validar(Procedimiento r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre"));
    }

    public List<Procedimiento> getProcedimientosActivos() {
        return findAllActivos();
    }

    public List<Procedimiento> findAllActivos() {
        return procedimientoDAO.findAllActivos();
    }

    public List<ProcedimientoPaso> findPasosByProcedimiento(UUID idProcedimiento) {
        if (idProcedimiento == null) return List.of();
        return pasoDAO.findByProcedimiento(idProcedimiento);
    }

    public List<ProcedimientoPasoSecuencia> findSecuenciasByProcedimiento(UUID idProcedimiento) {
        if (idProcedimiento == null) return List.of();
        return secuenciaDAO.findByProcedimiento(idProcedimiento);
    }

    public ProcedimientoDAO getProcedimientoDAO() {
        return procedimientoDAO;
    }

    public void setProcedimientoDAO(ProcedimientoDAO procedimientoDAO) {
        this.procedimientoDAO = procedimientoDAO;
    }

    public ProcedimientoPasoDAO getPasoDAO() {
        return pasoDAO;
    }

    public void setPasoDAO(ProcedimientoPasoDAO pasoDAO) {
        this.pasoDAO = pasoDAO;
    }

    public ProcedimientoPasoSecuenciaDAO getSecuenciaDAO() {
        return secuenciaDAO;
    }

    public void setSecuenciaDAO(ProcedimientoPasoSecuenciaDAO secuenciaDAO) {
        this.secuenciaDAO = secuenciaDAO;
    }
}
