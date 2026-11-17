package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
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

    // Las anotaciones propias evitan heredar el GET genérico como una segunda ruta.
    @Override
    public PageResponse<PersonaDTO> listar(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("20") int max,
            @QueryParam("q") String q) {
        return listar(first, max, q, null, null);
    }

    @GET
    @PermitAll
    @Operation(summary = "Lista personas con filtros opcionales por tipo y valor exacto del documento")
    public PageResponse<PersonaDTO> listar(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("20") int max,
            @QueryParam("q") String q,
            @QueryParam("tipoDocumentoId") UUID tipoDocumentoId,
            @QueryParam("valor") String valor) {
        if (first < 0) {
            throw new BadRequestException("El parámetro first debe ser mayor o igual a 0.");
        }
        if (max < 1) {
            throw new BadRequestException("El parámetro max debe ser mayor a 0.");
        }
        max = Math.min(max, 100);
        if (tipoDocumentoId == null && valor == null) {
            return super.listar(first, max, q);
        }
        String filtro = q == null || q.isBlank() ? null : q.trim();
        var items = personaService.findRange(first, max, filtro, tipoDocumentoId, valor).stream()
                .map(personaMapper::toDTO)
                .toList();
        long total = personaService.count(filtro, tipoDocumentoId, valor);
        return new PageResponse<>(items, total, first, max);
    }

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
