package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.TransactionalException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Optional;
import java.util.logging.Logger;

@Provider
public class TransactionalMapper implements ExceptionMapper<TransactionalException> {

    private static final int MAX_NIVELES_CAUSA = 10;
    private static final Logger LOG = Logger.getLogger(TransactionalMapper.class.getName());

    @Override
    public Response toResponse(TransactionalException exception) {
        Optional<ConstraintViolationException> validacion
                = ErrorUtils.buscarCausa(exception, ConstraintViolationException.class, MAX_NIVELES_CAUSA);
        if (validacion.isPresent()) {
            return ErrorUtils.respuestaValidacion(validacion.get());
        }
        Optional<PersistenceException> persistencia
                = ErrorUtils.buscarCausa(exception, PersistenceException.class, MAX_NIVELES_CAUSA);
        if (persistencia.isPresent()) {
            return ErrorUtils.respuestaPersistencia(persistencia.get());
        }
        return ErrorUtils.respuestaInterna(exception, LOG);
    }
}
