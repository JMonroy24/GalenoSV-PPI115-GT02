package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.logging.Logger;

@Provider
public class WebApplicationMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOG = Logger.getLogger(WebApplicationMapper.class.getName());

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response original = exception.getResponse();
        if (original == null) {
            return ErrorUtils.respuestaInterna(exception, LOG);
        }
        int estado = original.getStatus();
        ApiErrorCode codigo = ApiErrorCode.fromStatus(estado);
        ApiErrorResponse cuerpo;
        if (estado >= 500) {
            cuerpo = new ApiErrorResponse(estado, codigo.getCode(), codigo.getDefaultMessage(),
                    ErrorUtils.registrarErrorInterno(exception, LOG));
        } else {
            boolean estadoConocido = codigo.getStatusCode() == estado;
            String defecto = estadoConocido ? codigo.getDefaultMessage()
                    : "La solicitud falló con estado HTTP " + estado + ".";
            String mensaje = exception.getCause() == null ? ErrorUtils.mensaje(exception, defecto) : defecto;
            cuerpo = new ApiErrorResponse(estado, estadoConocido ? codigo.getCode() : "HTTP_ERROR", mensaje, Map.of());
        }
        return Response.fromResponse(original).entity(cuerpo).type(MediaType.APPLICATION_JSON)
                .header("Content-Length", null).build();
    }
}
