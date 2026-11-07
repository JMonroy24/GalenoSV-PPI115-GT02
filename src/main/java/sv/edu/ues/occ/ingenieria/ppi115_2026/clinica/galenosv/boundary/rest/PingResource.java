package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Diagnóstico", description = "Verificación de disponibilidad del API.")
@Path("/ping")
public class PingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Verifica que el API responde")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "El API está disponible."),
        @APIResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public Map<String, String> ping() {
        return Map.of("status", "ok");
    }
}
