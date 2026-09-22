package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

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
        return new PersonaRol();
    }

    public PersonaRolDAO getPersonaRolDAO() {
        return personaRolDAO;
    }

    public void setPersonaRolDAO(PersonaRolDAO personaRolDAO) {
        this.personaRolDAO = personaRolDAO;
    }
}
