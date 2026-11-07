package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class PreconditionRequiredMapper implements ExceptionMapper<PreconditionRequiredException> {

    @Override
    public Response toResponse(PreconditionRequiredException exception) {
        return Response.status(ApiErrorCode.PRECONDITION_REQUIRED.getStatusCode())
                .entity(ApiErrorResponse.of(ApiErrorCode.PRECONDITION_REQUIRED, exception.getMessage(), Map.of()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
