package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.json.bind.annotation.JsonbProperty;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiErrorResponse {

    @JsonbProperty("timestamp")
    private final String timestamp;

    @JsonbProperty("status_code")
    private final int statusCode;

    @JsonbProperty("error_code")
    private final String errorCode;

    @JsonbProperty("message")
    private final String message;

    @JsonbProperty("details")
    private final Map<String, Object> details;

    public ApiErrorResponse(int statusCode, String errorCode, String message, Map<String, Object> details) {
        this.timestamp = Instant.now().toString();
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
        Map<String, Object> copia = details == null ? Map.of() : details;
        this.details = Collections.unmodifiableMap(new LinkedHashMap<>(copia));
    }

    public static ApiErrorResponse of(ApiErrorCode code, String message, Map<String, Object> details) {
        String mensaje = message == null || message.isBlank() ? code.getDefaultMessage() : message;
        return new ApiErrorResponse(code.getStatusCode(), code.getCode(), mensaje, details);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
