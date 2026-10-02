package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Backing bean JSF para la gestión de la entidad PersonaRol.
 */
@Named("personaRolModel")
@ViewScoped
public class PersonaRolModel extends ModelTransaccional<PersonaRol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected PersonaRolDAO personaRolDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<PersonaRol, UUID> getDAO() {
        return personaRolDAO;
    }

    @Override
    protected PersonaRol crearNuevoRegistro() {
        return new PersonaRol(UUID.randomUUID());
    }

    public PersonaRolDAO getPersonaRolDAO() {
        return personaRolDAO;
    }

    public void setPersonaRolDAO(PersonaRolDAO personaRolDAO) {
        this.personaRolDAO = personaRolDAO;
    }

    @Inject
    protected sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaDAO personaDAO;

    public java.util.List<sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona> completePersona(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return personaDAO.buscarParaAutocompletar(query, 10);
    }


    @Override
    protected void validarNegocio(PersonaRol registro) {
        var persona = ValidadorComun.requerido(registro.getIdPersona(), "Seleccione una persona.");
        var rol = ValidadorComun.requerido(registro.getIdRol(), "Seleccione un rol.");
        ValidadorComun.requerido(persona.getIdPersona(), "Seleccione una persona válida.");
        ValidadorComun.requerido(rol.getIdRol(), "Seleccione un rol válido.");
        ValidadorComun.activo(rol.getActivo(), "El rol");
        UUID clinica = null;
        if (registro.getIdClinica() != null) {
            clinica = ValidadorComun.requerido(registro.getIdClinica().getIdClinica(), "Seleccione una clínica válida.");
            ValidadorComun.activo(registro.getIdClinica().getActivo(), "La clínica");
        }
        if (personaRolDAO.existeAsignacion(persona.getIdPersona(), rol.getIdRol(), clinica, registro.getIdPersonaRol())) {
            throw new ValidacionNegocioException("La persona ya tiene este rol en la clínica seleccionada.");
        }
    }

}
