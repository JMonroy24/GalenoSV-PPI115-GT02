package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

@ApplicationScoped
public class PersonaService extends DefaultService<Persona, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected PersonaDAO personaDAO;

    public PersonaService() {
    }

    public PersonaService(PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }

    @Override
    protected DefaultDAO<Persona, UUID> getDAO() {
        return personaDAO;
    }

    @Override
    protected void validar(Persona r, boolean nuevo) {
        r.setNombres(ValidadorComun.textoObligatorio(r.getNombres(), "El campo Nombres"));
        r.setApellidos(ValidadorComun.textoObligatorio(r.getApellidos(), "El campo Apellidos"));
        if (r.getFechaNacimiento() != null && !r.getFechaNacimiento().before(getHoy())) {
            throw new ValidacionNegocioException("La fecha de nacimiento debe estar en el pasado.");
        }
    }

    @Override
    public List<Persona> buscarParaAutocompletar(String filtro, int max) {
        return personaDAO.buscarParaAutocompletar(filtro, max);
    }

    public Date getHoy() {
        var zona = java.time.ZoneId.of("America/El_Salvador");
        return Date.from(java.time.LocalDate.now(zona).atStartOfDay(zona).toInstant());
    }

    public PersonaDAO getPersonaDAO() {
        return personaDAO;
    }

    public void setPersonaDAO(PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }
}
