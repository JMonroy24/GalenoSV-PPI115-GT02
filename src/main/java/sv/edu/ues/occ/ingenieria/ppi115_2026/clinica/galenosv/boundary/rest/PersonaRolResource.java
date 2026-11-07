package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaRolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.PersonaRolMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

@Tag(name = "Personas por rol", description = "Asignación de roles a personas.")
@jakarta.ws.rs.Path("/personas-roles")
@ApplicationScoped
public class PersonaRolResource extends GenericResource<PersonaRol, PersonaRolDTO, UUID> {

    @Inject
    PersonaRolService personaRolService;

    @Inject
    PersonaRolMapper personaRolMapper;

    @Override
    protected DefaultService<PersonaRol, UUID> getService() {
        return personaRolService;
    }

    @Override
    protected EntityMapper<PersonaRol, PersonaRolDTO> getMapper() {
        return personaRolMapper;
    }

    @Override
    protected UUID getId(PersonaRolDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(PersonaRol entidad, UUID id) {
        entidad.setIdPersonaRol(id);
    }

    @Override
    protected void asignarNuevoId(PersonaRol entidad) {
        entidad.setIdPersonaRol(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
