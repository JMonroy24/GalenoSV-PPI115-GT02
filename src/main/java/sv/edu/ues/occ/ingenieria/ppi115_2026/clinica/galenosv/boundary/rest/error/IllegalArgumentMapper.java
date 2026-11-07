package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class IllegalArgumentMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.status(ApiErrorCode.BAD_REQUEST.getStatusCode())
                .entity(ApiErrorResponse.of(ApiErrorCode.BAD_REQUEST, exception.getMessage(), Map.of()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
