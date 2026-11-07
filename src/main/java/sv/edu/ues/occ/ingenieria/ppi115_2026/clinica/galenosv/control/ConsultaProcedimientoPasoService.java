package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

@ApplicationScoped
public class ConsultaProcedimientoPasoService extends DefaultService<ConsultaProcedimientoPaso, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

    public ConsultaProcedimientoPasoService() {
    }

    public ConsultaProcedimientoPasoService(ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO) {
        this.consultaProcedimientoPasoDAO = consultaProcedimientoPasoDAO;
    }

    @Override
    protected DefaultDAO<ConsultaProcedimientoPaso, UUID> getDAO() {
        return consultaProcedimientoPasoDAO;
    }

    @Override
    protected void validar(ConsultaProcedimientoPaso r, boolean nuevo) {
        ValidadorComun.rangoFechas(r.getFechaInicio(), r.getFechaFin());
        r.setEstado(ValidadorComun.textoObligatorio(r.getEstado(), "El estado"));
    }

    @Override
    public List<ConsultaProcedimientoPaso> buscarParaAutocompletar(String filtro, int max) {
        return consultaProcedimientoPasoDAO.buscarParaAutocompletar(filtro, max);
    }

    public List<ConsultaProcedimientoPaso> findByConsultaProcedimiento(UUID idConsultaProcedimiento) {
        if (idConsultaProcedimiento == null) {
            return List.of();
        }
        return consultaProcedimientoPasoDAO.findByConsultaProcedimiento(idConsultaProcedimiento);
    }

    public ConsultaProcedimientoPasoDAO getConsultaProcedimientoPasoDAO() {
        return consultaProcedimientoPasoDAO;
    }

    public void setConsultaProcedimientoPasoDAO(ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO) {
        this.consultaProcedimientoPasoDAO = consultaProcedimientoPasoDAO;
    }
}
