package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
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

    public List<ConsultaProcedimientoPaso> findByConsultaProcedimiento(UUID idConsultaProcedimiento) {
        if (idConsultaProcedimiento == null) {
            return List.of();
        }
        return consultaProcedimientoPasoDAO.findByConsultaProcedimiento(idConsultaProcedimiento);
    }

    @Transactional
    public ConsultaProcedimientoPaso actualizarFormulario(UUID id,
            Predicate<ConsultaProcedimientoPaso> precondicion, String estadoPaso, String valor) {
        return actualizarSi(id, precondicion, paso -> {
            paso.setEstado(estadoPaso);
            paso.setValor(valor);
        });
    }

    public ConsultaProcedimientoPasoDAO getConsultaProcedimientoPasoDAO() {
        return consultaProcedimientoPasoDAO;
    }

    public void setConsultaProcedimientoPasoDAO(ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO) {
        this.consultaProcedimientoPasoDAO = consultaProcedimientoPasoDAO;
    }
}
