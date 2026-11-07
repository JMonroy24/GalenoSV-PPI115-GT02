package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return ErrorUtils.respuestaValidacion(exception);
    }
}
