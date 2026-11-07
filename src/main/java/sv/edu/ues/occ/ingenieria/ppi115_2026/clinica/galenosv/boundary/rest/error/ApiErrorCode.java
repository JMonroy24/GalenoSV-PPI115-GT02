package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.core.Response;

public enum ApiErrorCode {

    VALIDATION_ERROR(Response.Status.BAD_REQUEST,
            "Los datos enviados no son válidos."),
    ENTITY_NOT_FOUND(Response.Status.NOT_FOUND,
            "El recurso solicitado no existe."),
    PRECONDITION_FAILED(412,
            "El recurso fue modificado por otra operación. Actualice los datos e intente nuevamente."),
    PRECONDITION_REQUIRED(428,
            "Se requiere el encabezado If-Match para modificar el recurso."),
    CONFLICT(Response.Status.CONFLICT,
            "La operación entra en conflicto con el estado actual de los datos."),
    UNPROCESSABLE_ENTITY(422,
            "Regla de negocio incumplida."),
    BAD_REQUEST(Response.Status.BAD_REQUEST,
            "La solicitud no es válida."),
    UNAUTHORIZED(Response.Status.UNAUTHORIZED,
            "Se requiere autenticación para acceder al recurso."),
    FORBIDDEN(Response.Status.FORBIDDEN,
            "No tiene permisos para realizar esta operación."),
    METHOD_NOT_ALLOWED(Response.Status.METHOD_NOT_ALLOWED,
            "El método HTTP no está permitido para este recurso."),
    UNSUPPORTED_MEDIA_TYPE(Response.Status.UNSUPPORTED_MEDIA_TYPE,
            "El tipo de contenido enviado no es soportado."),
    INTERNAL_ERROR(Response.Status.INTERNAL_SERVER_ERROR,
            "Ocurrió un error interno. Intente nuevamente o contacte al administrador."),
    SERVICE_UNAVAILABLE(Response.Status.SERVICE_UNAVAILABLE,
            "El servicio no está disponible temporalmente.");

    private final Response.Status status;
    private final int statusCode;
    private final String defaultMessage;

    ApiErrorCode(Response.Status status, String defaultMessage) {
        this.status = status;
        this.statusCode = status.getStatusCode();
        this.defaultMessage = defaultMessage;
    }

    ApiErrorCode(int statusCode, String defaultMessage) {
        this.status = Response.Status.fromStatusCode(statusCode);
        this.statusCode = statusCode;
        this.defaultMessage = defaultMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getCode() {
        return name();
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public static ApiErrorCode fromStatus(int statusCode) {
        return switch (statusCode) {
            case 400 -> BAD_REQUEST;
            case 401 -> UNAUTHORIZED;
            case 403 -> FORBIDDEN;
            case 404 -> ENTITY_NOT_FOUND;
            case 405 -> METHOD_NOT_ALLOWED;
            case 409 -> CONFLICT;
            case 412 -> PRECONDITION_FAILED;
            case 415 -> UNSUPPORTED_MEDIA_TYPE;
            case 428 -> PRECONDITION_REQUIRED;
            case 422 -> UNPROCESSABLE_ENTITY;
            case 503 -> SERVICE_UNAVAILABLE;
            default -> statusCode >= 500 ? INTERNAL_ERROR : BAD_REQUEST;
        };
    }
}
