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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

/**
 * Backing bean JSF para la gestión de la entidad Persona.
 */
@Named("personaModel")
@ViewScoped
public class PersonaModel extends ModelTransaccional<Persona, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected PersonaDAO personaDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<Persona, UUID> getDAO() {
        return personaDAO;
    }

    @Override
    protected Persona crearNuevoRegistro() {
        return new Persona(UUID.randomUUID());
    }

    public PersonaDAO getPersonaDAO() {
        return personaDAO;
    }

    public void setPersonaDAO(PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }

    @Override
    protected void validarNegocio(Persona registro) {
        registro.setNombres(ValidadorComun.textoObligatorio(registro.getNombres(), "Los nombres"));
        registro.setApellidos(ValidadorComun.textoObligatorio(registro.getApellidos(), "Los apellidos"));
        if (registro.getFechaNacimiento() != null && !registro.getFechaNacimiento().isBefore(getHoy())) {
            throw new ValidacionNegocioException("La fecha de nacimiento debe estar en el pasado.");
        }
    }

    public java.time.LocalDate getHoy() {
        return java.time.LocalDate.now(java.time.ZoneId.of("America/El_Salvador"));
    }

}
