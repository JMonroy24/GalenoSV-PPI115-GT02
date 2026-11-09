package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

@ApplicationScoped
public class PersonaRolService extends DefaultService<PersonaRol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ROL_PACIENTE = PersonaRolDAO.ROL_PACIENTE;

    @Inject
    protected PersonaRolDAO personaRolDAO;

    public PersonaRolService() {
    }

    public PersonaRolService(PersonaRolDAO personaRolDAO) {
        this.personaRolDAO = personaRolDAO;
    }

    @Override
    protected DefaultDAO<PersonaRol, UUID> getDAO() {
        return personaRolDAO;
    }

    @Override
    protected void validar(PersonaRol r, boolean nuevo) {
        if (nuevo && r.getFechaCreacion() == null) {
            r.setFechaCreacion(new Date());
        }
        var persona = ValidadorComun.requerido(r.getIdPersona(), "Seleccione una persona.");
        var rol = ValidadorComun.requerido(r.getIdRol(), "Seleccione un rol.");
        ValidadorComun.requerido(persona.getIdPersona(), "Seleccione una persona válida.");
        ValidadorComun.requerido(rol.getIdRol(), "Seleccione un rol válido.");
        ValidadorComun.activo(rol.getActivo(), "El rol");
        var asignada = ValidadorComun.requerido(r.getIdClinica(), "Seleccione la clínica del rol.");
        UUID clinica = ValidadorComun.requerido(asignada.getIdClinica(), "Seleccione una clínica válida.");
        ValidadorComun.activo(asignada.getActivo(), "La clínica");
        if (personaRolDAO.existeAsignacion(persona.getIdPersona(), rol.getIdRol(), clinica, r.getIdPersonaRol())) {
            throw new ValidacionNegocioException("La persona ya tiene este rol en la clínica seleccionada.");
        }
    }

    @Override
    public List<PersonaRol> buscarParaAutocompletar(String filtro, int max) {
        return personaRolDAO.buscarParaAutocompletar(filtro, max);
    }

    public List<PersonaRol> findByClinicaAndRol(UUID idClinica, UUID idRol) {
        if (idClinica == null || idRol == null) {
            return List.of();
        }
        return personaRolDAO.findByClinicaAndRol(idClinica, idRol);
    }

    public boolean existeAsignacion(UUID idPersona, UUID idRol, UUID idClinica, UUID excluirId) {
        return personaRolDAO.existeAsignacion(idPersona, idRol, idClinica, excluirId);
    }

    public List<PersonaRol> findByPersona(UUID idPersona) {
        if (idPersona == null) {
            return List.of();
        }
        return personaRolDAO.findByPersona(idPersona);
    }

    public List<PersonaRol> buscarPacientes(String filtro, UUID idClinica, int max) {
        return personaRolDAO.buscarPacientes(filtro, idClinica, max);
    }

    public PersonaRol findResponsable(UUID idClinica, UUID idRol) {
        return personaRolDAO.findResponsable(idClinica, idRol);
    }

    public PersonaRolDAO getPersonaRolDAO() {
        return personaRolDAO;
    }

    public void setPersonaRolDAO(PersonaRolDAO personaRolDAO) {
        this.personaRolDAO = personaRolDAO;
    }
}
