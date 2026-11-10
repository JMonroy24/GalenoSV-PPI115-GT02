package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;
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

    private static final Logger LOG = Logger.getLogger(ErrorUtils.class.getName());

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
                String campo = ultimoNodo(violacion.getPropertyPath());
                String motivo = mensajeDeViolacion(violacion);
                detalles.merge(campo, motivo, (anterior, siguiente) -> anterior + ". " + siguiente);
            }
        }
        return respuesta(ApiErrorCode.VALIDATION_ERROR, null, detalles);
    }

    private static String mensajeDeViolacion(ConstraintViolation<?> violacion) {
        String mensaje = violacion.getMessage();
        return mensaje == null || mensaje.isBlank() ? "Valor inválido" : mensaje;
    }

    public static Response respuestaPersistencia(PersistenceException exception) {
        var validacion = buscarCausa(exception, ConstraintViolationException.class);
        if (validacion.isPresent()) {
            return respuestaValidacion(validacion.get());
        }
        if (buscarCausa(exception, OptimisticLockException.class).isPresent()
                || buscarCausa(exception, PessimisticLockException.class).isPresent()
                || buscarCausa(exception, LockTimeoutException.class).isPresent()) {
            return respuesta(ApiErrorCode.CONFLICT,
                    "Otra operación modificó o bloqueó el registro. Vuelva a intentarlo.", Map.of());
        }
        if (buscarCausa(exception, QueryTimeoutException.class).isPresent()) {
            return respuesta(ApiErrorCode.SERVICE_UNAVAILABLE,
                    "La base de datos tardó demasiado en responder.", Map.of());
        }
        String estado = buscarCausa(exception, SQLException.class).map(SQLException::getSQLState).orElse("");
        if (estado.startsWith("08")) {
            return respuesta(ApiErrorCode.SERVICE_UNAVAILABLE,
                    "No se pudo establecer o mantener la conexión con la base de datos.", Map.of());
        }
        return switch (estado) {
            case "23505" -> respuesta(ApiErrorCode.CONFLICT,
                    "Ya existe un registro con los mismos datos únicos.", Map.of());
            case "23503" -> respuesta(ApiErrorCode.CONFLICT,
                    "Una referencia no existe o hay registros relacionados que impiden la operación.", Map.of());
            case "23502" -> respuesta(ApiErrorCode.UNPROCESSABLE_ENTITY,
                    "Falta un dato obligatorio para guardar el registro.", Map.of());
            case "23514", "22001" -> respuesta(ApiErrorCode.UNPROCESSABLE_ENTITY,
                    "Un valor incumple una restricción de los datos.", Map.of());
            case "40001", "40P01", "55P03" -> respuesta(ApiErrorCode.CONFLICT,
                    "Una transacción concurrente impidió completar la operación. Vuelva a intentarlo.", Map.of());
            default -> respuestaInterna(exception, LOG, "No se pudo completar la operación en la base de datos.");
        };
    }

    public static Response respuestaInterna(Throwable exception, Logger logger) {
        return respuestaInterna(exception, logger, null);
    }

    private static Response respuestaInterna(Throwable exception, Logger logger, String mensaje) {
        return respuesta(ApiErrorCode.INTERNAL_ERROR, mensaje, registrarErrorInterno(exception, logger));
    }

    static Map<String, Object> registrarErrorInterno(Throwable exception, Logger logger) {
        String correlationId = UUID.randomUUID().toString();
        if (logger != null) {
            logger.log(Level.SEVERE, "Error interno. correlationId=" + correlationId, exception);
        }
        return Map.of("correlationId", correlationId);
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
