package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

    public List<Consulta> findRange(int first, int max, String filtroGlobal, UUID pacienteId) {
        return consultaDAO.findRange(first, max, filtroGlobal, List.of(), filtrosPorPaciente(pacienteId));
    }

    public long count(String filtroGlobal, UUID pacienteId) {
        return consultaDAO.count(filtroGlobal, filtrosPorPaciente(pacienteId));
    }

    private List<FiltroConsulta> filtrosPorPaciente(UUID pacienteId) {
        return pacienteId == null ? List.of()
                : List.of(new FiltroConsulta("idPersonaRol.idPersonaRol", pacienteId, "EQUALS"));
    }

    @Override
    protected void validar(Consulta r, boolean nuevo) {
        ValidadorComun.rangoFechas(r.getFechaInicio(), r.getFechaFin());
        var personaRol = r.getIdPersonaRol();
        if (personaRol == null) {
            throw new ValidacionNegocioException("La persona debe ser paciente.");
        }
        var rol = personaRol.getIdRol();
        if (rol == null) {
            throw new ValidacionNegocioException("La persona debe ser paciente.");
        }
        if (!Boolean.TRUE.equals(rol.getActivo())) {
            throw new ValidacionNegocioException("La persona debe ser paciente.");
        }
        if (rol.getNombre() == null
                || !rol.getNombre().toLowerCase(java.util.Locale.ROOT).contains(PersonaRolService.ROL_PACIENTE)) {
            throw new ValidacionNegocioException("La persona debe ser paciente.");
        }
    }

    public List<Consulta> findRangeFiltrado(int first, int max, UUID idClinica, Date desde, Date hasta) {
        return consultaDAO.findRangeFiltrado(first, max, idClinica, desde, hasta);
    }

    public List<Consulta> buscarParaAutocompletar(String filtro, int max, UUID idClinica) {
        String texto = normalizarFiltro(filtro);
        if (texto == null || texto.length() < 2 || idClinica == null) return List.of();
        return consultaDAO.buscarParaAutocompletar(texto, max, idClinica);
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

    @Transactional
    public void crear(Consulta c, UUID idClinica) {
        validarClinica(c, idClinica);
        crear(c);
    }

    @Transactional
    public void actualizar(Consulta c, UUID idClinica) {
        validarClinica(c, idClinica);
        actualizar(c);
    }

    private void validarClinica(Consulta c, UUID idClinica) {
        if (idClinica == null) {
            throw new ValidacionNegocioException("Seleccione una cl\u00ednica de trabajo antes de crear consultas.");
        }
        var personaRol = c.getIdPersonaRol();
        if (personaRol == null || personaRol.getIdClinica() == null
                || !idClinica.equals(personaRol.getIdClinica().getIdClinica())) {
            throw new ValidacionNegocioException("La persona debe ser paciente de la cl\u00ednica de trabajo.");
        }
    }

    public ConsultaDAO getConsultaDAO() {
        return consultaDAO;
    }

    public void setConsultaDAO(ConsultaDAO consultaDAO) {
        this.consultaDAO = consultaDAO;
    }
}
