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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Backing bean JSF para la gestión de la entidad PersonaRol.
 */
@Named("personaRolModel")
@ViewScoped
public class PersonaRolModel extends ModelTransaccional<PersonaRol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected PersonaRolService personaRolService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<PersonaRol, UUID> getDAO() {
        return personaRolService;
    }

    @Override
    protected PersonaRol crearNuevoRegistro() {
        return new PersonaRol(UUID.randomUUID());
    }

    public PersonaRolService getPersonaRolService() {
        return personaRolService;
    }

    public void setPersonaRolService(PersonaRolService personaRolService) {
        this.personaRolService = personaRolService;
    }

    @Inject
    protected PersonaService personaService;

    public java.util.List<sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona> completePersona(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return personaService.buscarParaAutocompletar(query, 10);
    }


    @Override
    protected void validarNegocio(PersonaRol registro) {
        var persona = ValidadorComun.requerido(registro.getIdPersona(), "Seleccione una persona.");
        var rol = ValidadorComun.requerido(registro.getIdRol(), "Seleccione un rol.");
        ValidadorComun.requerido(persona.getIdPersona(), "Seleccione una persona válida.");
        ValidadorComun.requerido(rol.getIdRol(), "Seleccione un rol válido.");
        ValidadorComun.activo(rol.getActivo(), "El rol");
        var asignada = ValidadorComun.requerido(registro.getIdClinica(), "Seleccione la clínica del rol.");
        UUID clinica = ValidadorComun.requerido(asignada.getIdClinica(), "Seleccione una clínica válida.");
        ValidadorComun.activo(asignada.getActivo(), "La clínica");
        if (personaRolService.existeAsignacion(persona.getIdPersona(), rol.getIdRol(), clinica, registro.getIdPersonaRol())) {
            throw new ValidacionNegocioException("La persona ya tiene este rol en la clínica seleccionada.");
        }
    }

}
