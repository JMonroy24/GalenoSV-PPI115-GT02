package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class PreconditionFailedMapper implements ExceptionMapper<PreconditionFailedException> {

    @Override
    public Response toResponse(PreconditionFailedException exception) {
        return Response.status(ApiErrorCode.PRECONDITION_FAILED.getStatusCode())
                .entity(ApiErrorResponse.of(ApiErrorCode.PRECONDITION_FAILED, exception.getMessage(), Map.of()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
