package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.TransactionalException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionMappersTest {

    private ApiErrorResponse cuerpo(Response respuesta) {
        return (ApiErrorResponse) respuesta.getEntity();
    }

    @Test
    void constraintViolationMapperDevuelve400ConDetallesPorCampo() {
        ConstraintViolation<Object> violacion = mock(ConstraintViolation.class);
        Path ruta = mock(Path.class);
        Path.Node nodo = mock(Path.Node.class);
        when(violacion.getPropertyPath()).thenReturn(ruta);
        when(violacion.getMessage()).thenReturn("no debe estar vacío");
        when(ruta.iterator()).thenReturn(iteratorDe(nodo));
        when(nodo.getName()).thenReturn("nombre");

        ConstraintViolationException excepcion = new ConstraintViolationException(Set.of(violacion));
        Response respuesta = new ConstraintViolationMapper().toResponse(excepcion);

        assertEquals(400, respuesta.getStatus());
        ApiErrorResponse cuerpo = cuerpo(respuesta);
        assertEquals("VALIDATION_ERROR", cuerpo.getErrorCode());
        assertEquals("no debe estar vacío", cuerpo.getDetails().get("nombre"));
    }

    @Test
    void validacionNegocioMapperDevuelve422() {
        Response respuesta = new ValidacionNegocioMapper()
                .toResponse(new ValidacionNegocioException("Regla incumplida"));

        assertEquals(422, respuesta.getStatus());
        assertEquals("UNPROCESSABLE_ENTITY", cuerpo(respuesta).getErrorCode());
        assertEquals("Regla incumplida", cuerpo(respuesta).getMessage());
    }

    @Test
    void illegalArgumentMapperDevuelve400YMuestraMensaje() {
        Response respuesta = new IllegalArgumentMapper()
                .toResponse(new IllegalArgumentException("dato inválido"));

        assertEquals(400, respuesta.getStatus());
        assertEquals("BAD_REQUEST", cuerpo(respuesta).getErrorCode());
        assertEquals("dato inválido", cuerpo(respuesta).getMessage());
    }

    @Test
    void notFoundMapperDevuelve404() {
        Response respuesta = new NotFoundMapper().toResponse(new NotFoundException());

        assertEquals(404, respuesta.getStatus());
        assertEquals("ENTITY_NOT_FOUND", cuerpo(respuesta).getErrorCode());
    }

    @Test
    void webApplicationMapperUsaElStatusDeLaExcepcion() {
        WebApplicationException excepcion = new WebApplicationException(
                Response.status(Response.Status.CONFLICT).build());
        Response respuesta = new WebApplicationMapper().toResponse(excepcion);

        assertEquals(409, respuesta.getStatus());
        assertEquals("CONFLICT", cuerpo(respuesta).getErrorCode());
    }

    @Test
    void persistenceMapperDevuelve409SinFiltrarDetallesTecnicos() {
        PersistenceException excepcion = new PersistenceException(
                "SELECT * FROM clinica WHERE password = 'secreto'");
        Response respuesta = new PersistenceMapper().toResponse(excepcion);

        assertEquals(409, respuesta.getStatus());
        ApiErrorResponse cuerpo = cuerpo(respuesta);
        assertEquals("CONFLICT", cuerpo.getErrorCode());
        assertFalse(cuerpo.getMessage().contains("SELECT"));
        assertFalse(cuerpo.getMessage().contains("clinica"));
        assertFalse(cuerpo.getMessage().contains("secreto"));
    }

    @Test
    void webApplicationMapperUsaElMensajeDeLaExcepcion() {
        WebApplicationException excepcion = new WebApplicationException("mensaje personalizado",
                Response.status(Response.Status.BAD_REQUEST).build());

        Response respuesta = new WebApplicationMapper().toResponse(excepcion);

        assertEquals(400, respuesta.getStatus());
        assertEquals("mensaje personalizado", cuerpo(respuesta).getMessage());
    }

    @Test
    void webApplicationMapperUsaElMensajePorDefectoSiElMensajeEsVacio() {
        WebApplicationException excepcion = new WebApplicationException((String) null,
                Response.status(Response.Status.BAD_REQUEST).build());

        Response respuesta = new WebApplicationMapper().toResponse(excepcion);

        assertEquals(400, respuesta.getStatus());
        assertEquals(ApiErrorCode.BAD_REQUEST.getDefaultMessage(), cuerpo(respuesta).getMessage());
    }

    @Test
    void webApplicationMapperUsaElMensajePorDefectoSiElMensajeEsStringVacio() {
        WebApplicationException excepcion = new WebApplicationException("   ",
                Response.status(Response.Status.BAD_REQUEST).build());

        Response respuesta = new WebApplicationMapper().toResponse(excepcion);

        assertEquals(400, respuesta.getStatus());
        assertEquals(ApiErrorCode.BAD_REQUEST.getDefaultMessage(), cuerpo(respuesta).getMessage());
    }

    @Test
    void persistenceMapperTraduceSqlState23505A409() {
        Response respuesta = new PersistenceMapper().toResponse(
                persistenciaConSqlState("23505"));

        assertEquals(409, respuesta.getStatus());
        assertEquals("CONFLICT", cuerpo(respuesta).getErrorCode());
        assertMensajeSeguro(cuerpo(respuesta).getMessage());
    }

    @Test
    void persistenceMapperTraduceSqlState23503A409() {
        Response respuesta = new PersistenceMapper().toResponse(
                persistenciaConSqlState("23503"));

        assertEquals(409, respuesta.getStatus());
        assertEquals("CONFLICT", cuerpo(respuesta).getErrorCode());
        assertMensajeSeguro(cuerpo(respuesta).getMessage());
    }

    @Test
    void persistenceMapperTraduceSqlState08A503() {
        Response respuesta = new PersistenceMapper().toResponse(
                persistenciaConSqlState("08006"));

        assertEquals(503, respuesta.getStatus());
        assertEquals("SERVICE_UNAVAILABLE", cuerpo(respuesta).getErrorCode());
        assertMensajeSeguro(cuerpo(respuesta).getMessage());
    }

    @Test
    void persistenceMapperTraduceOtroSqlStateA409Generico() {
        Response respuesta = new PersistenceMapper().toResponse(
                persistenciaConSqlState("40001"));

        assertEquals(409, respuesta.getStatus());
        assertEquals("CONFLICT", cuerpo(respuesta).getErrorCode());
        assertMensajeSeguro(cuerpo(respuesta).getMessage());
    }

    @Test
    void transactionalMapperDetectaConstraintViolationAnidada() {
        ConstraintViolation<Object> violacion = mock(ConstraintViolation.class);
        Path ruta = mock(Path.class);
        Path.Node nodo = mock(Path.Node.class);
        when(violacion.getPropertyPath()).thenReturn(ruta);
        when(violacion.getMessage()).thenReturn("campo obligatorio");
        when(ruta.iterator()).thenReturn(iteratorDe(nodo));
        when(nodo.getName()).thenReturn("nombre");

        ConstraintViolationException validacion
                = new ConstraintViolationException(Set.of(violacion));
        TransactionalException excepcion = new TransactionalException("tx",
                new RuntimeException("rollback", validacion));

        Response respuesta = new TransactionalMapper().toResponse(excepcion);

        assertEquals(400, respuesta.getStatus());
        assertEquals("VALIDATION_ERROR", cuerpo(respuesta).getErrorCode());
        assertEquals("campo obligatorio", cuerpo(respuesta).getDetails().get("nombre"));
    }

    @Test
    void transactionalMapperConCadenaAnidadaHastaSqlState23505Devuelve409() {
        SQLException sqlException = new SQLException(
                "duplicate key value violates unique constraint valor='secreto'", "23505");
        PersistenceException persistencia = new PersistenceException(
                "INSERT INTO clinica VALUES ('secreto')", sqlException);
        RollbackException rollback = new RollbackException("rollback");
        rollback.initCause(persistencia);
        TransactionalException excepcion = new TransactionalException("transaccion", rollback);

        Response respuesta = new TransactionalMapper().toResponse(excepcion);

        assertEquals(409, respuesta.getStatus());
        ApiErrorResponse cuerpo = cuerpo(respuesta);
        assertEquals("CONFLICT", cuerpo.getErrorCode());
        assertMensajeSeguro(cuerpo.getMessage());
    }

    @Test
    void transactionalMapperSinCausaConocidaDevuelve500ConCorrelationId() {
        TransactionalException excepcion = new TransactionalException("transaccion",
                new RuntimeException("mensaje sensible"));

        Response respuesta = new TransactionalMapper().toResponse(excepcion);

        assertEquals(500, respuesta.getStatus());
        ApiErrorResponse cuerpo = cuerpo(respuesta);
        assertEquals("INTERNAL_ERROR", cuerpo.getErrorCode());
        assertFalse(cuerpo.getMessage().contains("mensaje sensible"));
        Object correlationId = cuerpo.getDetails().get("correlationId");
        assertNotNull(correlationId);
        assertNotNull(UUID.fromString(correlationId.toString()));
    }

    @Test
    void transactionalMapperRespetaLimiteDe10Niveles() {
        Throwable actual = new ConstraintViolationException(Set.of());
        for (int i = 0; i < 10; i++) {
            actual = new RuntimeException("nivel " + i, actual);
        }
        TransactionalException excepcion = new TransactionalException("tx", actual);

        Response respuesta = new TransactionalMapper().toResponse(excepcion);

        assertEquals(500, respuesta.getStatus());
        assertEquals("INTERNAL_ERROR", cuerpo(respuesta).getErrorCode());
    }

    @Test
    void transactionalMapperAlcanzaCausaEnNivel10() {
        Throwable actual = new ConstraintViolationException(Set.of());
        for (int i = 0; i < 9; i++) {
            actual = new RuntimeException("nivel " + i, actual);
        }
        TransactionalException excepcion = new TransactionalException("tx", actual);

        Response respuesta = new TransactionalMapper().toResponse(excepcion);

        assertEquals(400, respuesta.getStatus());
        assertEquals("VALIDATION_ERROR", cuerpo(respuesta).getErrorCode());
    }

    @Test
    void genericExceptionMapperNoFiltraElMensajeEIncluyeCorrelationId() {
        Response respuesta = new GenericExceptionMapper()
                .toResponse(new RuntimeException("mensaje original sensible"));

        assertEquals(500, respuesta.getStatus());
        ApiErrorResponse cuerpo = cuerpo(respuesta);
        assertEquals("INTERNAL_ERROR", cuerpo.getErrorCode());
        assertFalse(cuerpo.getMessage().contains("mensaje original sensible"));

        Object correlationId = cuerpo.getDetails().get("correlationId");
        assertNotNull(correlationId);
        assertNotNull(UUID.fromString(correlationId.toString()));
    }

    private PersistenceException persistenciaConSqlState(String sqlState) {
        return new PersistenceException("detalle tecnico",
                new SQLException("SQL sensible con valores", sqlState));
    }

    private void assertMensajeSeguro(String mensaje) {
        assertFalse(mensaje.contains("SELECT"));
        assertFalse(mensaje.contains("INSERT"));
        assertFalse(mensaje.contains("clinica"));
        assertFalse(mensaje.contains("secreto"));
        assertFalse(mensaje.contains("SQL sensible"));
    }

    private Iterator<Path.Node> iteratorDe(Path.Node nodo) {
        return Map.of(1, nodo).values().iterator();
    }
}
