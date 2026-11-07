package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.PersonaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

@Tag(name = "Personas", description = "Datos de personas registradas.")
@jakarta.ws.rs.Path("/personas")
@ApplicationScoped
public class PersonaResource extends GenericResource<Persona, PersonaDTO, UUID> {

    @Inject
    PersonaService personaService;

    @Inject
    PersonaMapper personaMapper;

    @Override
    protected DefaultService<Persona, UUID> getService() {
        return personaService;
    }

    @Override
    protected EntityMapper<Persona, PersonaDTO> getMapper() {
        return personaMapper;
    }

    @Override
    protected UUID getId(PersonaDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(Persona entidad, UUID id) {
        entidad.setIdPersona(id);
    }

    @Override
    protected void asignarNuevoId(Persona entidad) {
        entidad.setIdPersona(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
