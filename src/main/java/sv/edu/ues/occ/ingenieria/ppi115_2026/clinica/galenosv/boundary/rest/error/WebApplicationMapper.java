package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class WebApplicationMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        int estado = exception.getResponse() == null
                ? Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
                : exception.getResponse().getStatus();
        ApiErrorCode codigo = ApiErrorCode.fromStatus(estado);
        return ErrorUtils.respuesta(codigo,
                ErrorUtils.mensaje(exception, codigo.getDefaultMessage()), Map.of());
    }
}
