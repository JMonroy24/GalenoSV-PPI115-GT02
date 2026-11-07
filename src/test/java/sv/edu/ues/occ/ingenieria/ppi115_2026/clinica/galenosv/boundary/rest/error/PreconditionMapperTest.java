package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PreconditionMapperTest {

    @Test
    void preconditionRequiredDevuelve428() {
        Response respuesta = new PreconditionRequiredMapper()
                .toResponse(new PreconditionRequiredException("Falta If-Match."));

        assertEquals(428, respuesta.getStatus());
        ApiErrorResponse cuerpo = (ApiErrorResponse) respuesta.getEntity();
        assertEquals(428, cuerpo.getStatusCode());
        assertEquals(ApiErrorCode.PRECONDITION_REQUIRED.getCode(), cuerpo.getErrorCode());
        assertEquals("Falta If-Match.", cuerpo.getMessage());
    }

    @Test
    void preconditionFailedDevuelve412() {
        Response respuesta = new PreconditionFailedMapper()
                .toResponse(new PreconditionFailedException("ETag no coincide."));

        assertEquals(412, respuesta.getStatus());
        ApiErrorResponse cuerpo = (ApiErrorResponse) respuesta.getEntity();
        assertEquals(412, cuerpo.getStatusCode());
        assertEquals(ApiErrorCode.PRECONDITION_FAILED.getCode(), cuerpo.getErrorCode());
        assertEquals("ETag no coincide.", cuerpo.getMessage());
    }

    @Test
    void fromStatusReconoce412Y428() {
        assertEquals(ApiErrorCode.PRECONDITION_FAILED, ApiErrorCode.fromStatus(412));
        assertEquals(ApiErrorCode.PRECONDITION_REQUIRED, ApiErrorCode.fromStatus(428));
    }
}
