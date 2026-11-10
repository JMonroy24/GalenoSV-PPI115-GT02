package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        var codigo = ApiErrorCode.ENTITY_NOT_FOUND;
        return ErrorUtils.respuesta(codigo, ErrorUtils.mensaje(exception, codigo.getDefaultMessage()), Map.of());
    }
}
