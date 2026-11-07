package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;

@Provider
public class ValidacionNegocioMapper implements ExceptionMapper<ValidacionNegocioException> {

    @Override
    public Response toResponse(ValidacionNegocioException exception) {
        return Response.status(ApiErrorCode.UNPROCESSABLE_ENTITY.getStatusCode())
                .entity(ApiErrorResponse.of(ApiErrorCode.UNPROCESSABLE_ENTITY, exception.getMessage(), Map.of()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
