package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.Cie10DTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.Cie10Service;

@Tag(name = "CIE-10", description = "Catálogo de códigos de diagnóstico CIE-10.")
@Path("/cie10")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class Cie10Resource {

    @Inject
    Cie10Service cie10Service;

    @GET
    @PermitAll
    @Operation(summary = "Lista códigos CIE-10 de forma paginada con filtro opcional")
    public PageResponse<Cie10DTO> listar(
            @QueryParam("q") String q,
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("20") int max) {
        if (first < 0) {
            throw new BadRequestException("El parámetro first debe ser mayor o igual a 0.");
        }
        if (max < 1) {
            throw new BadRequestException("El parámetro max debe ser mayor a 0.");
        }
        max = Math.min(max, 100);
        List<Cie10DTO> encontrados = cie10Service.buscar(q);
        List<Cie10DTO> items = first >= encontrados.size()
                ? List.of()
                : encontrados.subList(first, Math.min(first + max, encontrados.size()));
        return new PageResponse<>(items, encontrados.size(), first, max);
    }
}
