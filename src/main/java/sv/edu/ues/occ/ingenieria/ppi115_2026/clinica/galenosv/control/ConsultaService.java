package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

@ApplicationScoped
public class ConsultaService extends DefaultService<Consulta, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ConsultaDAO consultaDAO;

    public ConsultaService() {
    }

    public ConsultaService(ConsultaDAO consultaDAO) {
        this.consultaDAO = consultaDAO;
    }

    @Override
    protected DefaultDAO<Consulta, UUID> getDAO() {
        return consultaDAO;
    }

    @Override
    protected void validar(Consulta r, boolean nuevo) {
        ValidadorComun.rangoFechas(r.getFechaInicio(), r.getFechaFin());
    }

    @Override
    public List<Consulta> buscarParaAutocompletar(String filtro, int max) {
        return consultaDAO.buscarParaAutocompletar(filtro, max);
    }

    public List<Consulta> findRangeFiltrado(int first, int max, UUID idClinica, Date desde, Date hasta) {
        return consultaDAO.findRangeFiltrado(first, max, idClinica, desde, hasta);
    }

    public List<Consulta> findRangeFiltrado(int first, int max, UUID idClinica, Date desde, Date hasta, String filtro) {
        return consultaDAO.findRangeFiltrado(first, max, idClinica, desde, hasta, filtro);
    }

    public long countFiltrado(UUID idClinica, Date desde, Date hasta) {
        return consultaDAO.countFiltrado(idClinica, desde, hasta);
    }

    public long countFiltrado(UUID idClinica, Date desde, Date hasta, String filtro) {
        return consultaDAO.countFiltrado(idClinica, desde, hasta, filtro);
    }

    public ConsultaDAO getConsultaDAO() {
        return consultaDAO;
    }

    public void setConsultaDAO(ConsultaDAO consultaDAO) {
        this.consultaDAO = consultaDAO;
    }
}
