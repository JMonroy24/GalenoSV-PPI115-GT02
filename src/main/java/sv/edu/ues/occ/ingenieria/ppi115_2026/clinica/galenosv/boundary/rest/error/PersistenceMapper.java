package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Cubre PersistenceException y OptimisticLockException, ya que esta última
 * extiende de PersistenceException.
 */
@Provider
public class PersistenceMapper implements ExceptionMapper<PersistenceException> {

    @Override
    public Response toResponse(PersistenceException exception) {
        return ErrorUtils.respuestaPersistencia(exception);
    }
}
