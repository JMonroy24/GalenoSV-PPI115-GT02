package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        validarPaciente(r);
    }

    @Transactional
    public void crear(Consulta consulta, UUID idClinica) {
        validarClinica(consulta, idClinica);
        crear(consulta);
    }

    @Transactional
    public void actualizar(Consulta consulta, UUID idClinica) {
        validarClinica(consulta, idClinica);
        actualizar(consulta);
    }

    public void validarPacienteDeClinica(Consulta consulta, UUID idClinica) {
        validarClinica(consulta, idClinica);
        validarPaciente(consulta);
    }

    private void validarPaciente(Consulta consulta) {
        var personaRol = consulta.getIdPersonaRol();
        var rol = personaRol == null ? null : personaRol.getIdRol();
        if (rol == null || !Boolean.TRUE.equals(rol.getActivo()) || rol.getNombre() == null
                || !rol.getNombre().toLowerCase(Locale.ROOT).contains(PersonaRolService.ROL_PACIENTE)) {
            throw new ValidacionNegocioException("La persona debe ser paciente.");
        }
    }

    private void validarClinica(Consulta consulta, UUID idClinica) {
        ValidadorComun.requerido(idClinica, "Seleccione una clínica de trabajo antes de crear consultas.");
        var personaRol = consulta.getIdPersonaRol();
        var clinica = personaRol == null ? null : personaRol.getIdClinica();
        if (clinica == null || !idClinica.equals(clinica.getIdClinica())) {
            throw new ValidacionNegocioException("La persona debe ser paciente de la clínica de trabajo.");
        }
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
