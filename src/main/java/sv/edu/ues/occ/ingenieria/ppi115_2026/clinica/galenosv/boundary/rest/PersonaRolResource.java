package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaRolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.PersonaRolMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

@Tag(name = "Personas por rol", description = "Asignación de roles a personas.")
@jakarta.ws.rs.Path("/personas-roles")
@RequestScoped
public class PersonaRolResource extends GenericResource<PersonaRol, PersonaRolDTO, UUID> {

    @Inject
    PersonaRolService personaRolService;

    @Inject
    PersonaRolMapper personaRolMapper;

    @QueryParam("personaId")
    String personaId;

    @Override
    @GET
    @PermitAll
    @Operation(summary = "Lista los recursos de forma paginada con filtro opcional por personaId")
    public PageResponse<PersonaRolDTO> listar(
            @QueryParam("first") @jakarta.ws.rs.DefaultValue("0") int first,
            @QueryParam("max") @jakarta.ws.rs.DefaultValue("20") int max,
            @QueryParam("q") String q) {
        if (personaId == null) {
            return super.listar(first, max, q);
        }
        UUID id;
        try {
            id = UUID.fromString(personaId.trim());
            if (!id.toString().equalsIgnoreCase(personaId.trim())) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("El parámetro personaId debe ser un UUID válido.", ex);
        }
        if (first < 0) {
            throw new BadRequestException("El parámetro first debe ser mayor o igual a 0.");
        }
        if (max < 1) {
            throw new BadRequestException("El parámetro max debe ser mayor a 0.");
        }
        max = Math.min(max, 100);
        String filtro = q == null || q.isBlank() ? null : q.trim();
        var items = personaRolService.findRange(first, max, filtro, id).stream()
                .map(getMapper()::toDTO)
                .toList();
        long total = personaRolService.count(filtro, id);
        return new PageResponse<>(items, total, first, max);
    }

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
