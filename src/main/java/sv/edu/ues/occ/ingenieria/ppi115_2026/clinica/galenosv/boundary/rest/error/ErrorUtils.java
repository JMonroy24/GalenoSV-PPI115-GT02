package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ErrorUtils {

    private ErrorUtils() {
    }

    public static String mensaje(Throwable exception, String mensajePorDefecto) {
        if (exception == null) {
            return mensajePorDefecto;
        }
        String mensaje = exception.getMessage();
        return mensaje == null || mensaje.isBlank() ? mensajePorDefecto : mensaje;
    }

    public static Response respuesta(ApiErrorCode codigo, String mensaje, Map<String, Object> detalles) {
        return Response.status(codigo.getStatusCode())
                .entity(ApiErrorResponse.of(codigo, mensaje, detalles))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static <T extends Throwable> Optional<T> buscarCausa(Throwable origen, Class<T> tipo) {
        if (origen == null || tipo == null) {
            return Optional.empty();
        }
        Set<Throwable> visitados = Collections.newSetFromMap(new IdentityHashMap<>());
        Throwable actual = origen;
        while (actual != null && visitados.add(actual)) {
            if (tipo.isInstance(actual)) {
                return Optional.of(tipo.cast(actual));
            }
            actual = actual.getCause();
        }
        return Optional.empty();
    }

    public static <T extends Throwable> Optional<T> buscarCausa(Throwable origen, Class<T> tipo, int maxNiveles) {
        if (origen == null || tipo == null || maxNiveles < 1) {
            return Optional.empty();
        }
        Set<Throwable> visitados = Collections.newSetFromMap(new IdentityHashMap<>());
        Throwable actual = origen;
        if (tipo.isInstance(actual)) {
            return Optional.of(tipo.cast(actual));
        }
        visitados.add(actual);
        actual = actual.getCause();
        for (int nivel = 0; nivel < maxNiveles && actual != null && visitados.add(actual); nivel++) {
            if (tipo.isInstance(actual)) {
                return Optional.of(tipo.cast(actual));
            }
            actual = actual.getCause();
        }
        return Optional.empty();
    }

    public static Response respuestaValidacion(ConstraintViolationException exception) {
        Map<String, Object> detalles = new LinkedHashMap<>();
        if (exception != null && exception.getConstraintViolations() != null) {
            for (ConstraintViolation<?> violacion : exception.getConstraintViolations()) {
                detalles.put(ultimoNodo(violacion.getPropertyPath()), violacion.getMessage());
            }
        }
        return respuesta(ApiErrorCode.VALIDATION_ERROR, null, detalles);
    }

    public static Response respuestaPersistencia(PersistenceException exception) {
        Optional<SQLException> sqlException = buscarCausa(exception, SQLException.class);
        if (sqlException.isPresent()) {
            String sqlState = sqlException.get().getSQLState();
            if ("23505".equals(sqlState) || "23503".equals(sqlState)) {
                return respuesta(ApiErrorCode.CONFLICT, null, Map.of());
            }
            if (sqlState != null && sqlState.startsWith("08")) {
                return respuesta(ApiErrorCode.SERVICE_UNAVAILABLE, null, Map.of());
            }
        }
        return respuesta(ApiErrorCode.CONFLICT, null, Map.of());
    }

    public static Response respuestaInterna(Throwable exception, Logger logger) {
        String correlationId = UUID.randomUUID().toString();
        if (logger != null) {
            logger.log(Level.SEVERE, "Error interno. correlationId=" + correlationId, exception);
        }
        return respuesta(ApiErrorCode.INTERNAL_ERROR, null, Map.of("correlationId", correlationId));
    }

    private static String ultimoNodo(Path ruta) {
        String campo = "desconocido";
        if (ruta != null) {
            for (Path.Node nodo : ruta) {
                if (nodo.getName() != null) {
                    campo = nodo.getName();
                }
            }
        }
        return campo;
    }
}
