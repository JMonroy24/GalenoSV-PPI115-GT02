package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

@ApplicationScoped
public class ConsultaProcedimientoService extends DefaultService<ConsultaProcedimiento, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ConsultaProcedimientoDAO consultaProcedimientoDAO;

    public ConsultaProcedimientoService() {
    }

    public ConsultaProcedimientoService(ConsultaProcedimientoDAO consultaProcedimientoDAO) {
        this.consultaProcedimientoDAO = consultaProcedimientoDAO;
    }

    @Override
    protected DefaultDAO<ConsultaProcedimiento, UUID> getDAO() {
        return consultaProcedimientoDAO;
    }

    @Override
    protected void validar(ConsultaProcedimiento r, boolean nuevo) {
        ValidadorComun.rangoFechas(r.getFechaInicio(), r.getFechaFin());
        if (r.getIdConsulta() != null) {
            ValidadorComun.dentroDelPeriodo(r.getFechaInicio(), r.getFechaFin(),
                    r.getIdConsulta().getFechaInicio(), r.getIdConsulta().getFechaFin());
        }
        if (r.getIdProcedimiento() != null) {
            ValidadorComun.activo(r.getIdProcedimiento().getActivo(), "El procedimiento");
        }
    }

    @Override
    public List<ConsultaProcedimiento> buscarParaAutocompletar(String filtro, int max) {
        return consultaProcedimientoDAO.buscarParaAutocompletar(filtro, max);
    }

    public List<ConsultaProcedimiento> findByConsulta(UUID idConsulta) {
        if (idConsulta == null) {
            return List.of();
        }
        return consultaProcedimientoDAO.findByConsulta(idConsulta);
    }

    public ConsultaProcedimientoDAO getConsultaProcedimientoDAO() {
        return consultaProcedimientoDAO;
    }

    public void setConsultaProcedimientoDAO(ConsultaProcedimientoDAO consultaProcedimientoDAO) {
        this.consultaProcedimientoDAO = consultaProcedimientoDAO;
    }
}
