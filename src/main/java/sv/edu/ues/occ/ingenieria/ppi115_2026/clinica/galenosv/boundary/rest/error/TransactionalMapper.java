package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.TransactionalException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Optional;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;

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
        var negocio = ErrorUtils.buscarCausa(exception, ValidacionNegocioException.class, MAX_NIVELES_CAUSA);
        if (negocio.isPresent()) {
            return new ValidacionNegocioMapper().toResponse(negocio.get());
        }
        var requerida = ErrorUtils.buscarCausa(exception, PreconditionRequiredException.class, MAX_NIVELES_CAUSA);
        if (requerida.isPresent()) {
            return new PreconditionRequiredMapper().toResponse(requerida.get());
        }
        var fallida = ErrorUtils.buscarCausa(exception, PreconditionFailedException.class, MAX_NIVELES_CAUSA);
        if (fallida.isPresent()) {
            return new PreconditionFailedMapper().toResponse(fallida.get());
        }
        var web = ErrorUtils.buscarCausa(exception, WebApplicationException.class, MAX_NIVELES_CAUSA);
        return web.isPresent() ? new WebApplicationMapper().toResponse(web.get())
                : ErrorUtils.respuestaInterna(exception, LOG);
    }
}
