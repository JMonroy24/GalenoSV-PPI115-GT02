package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record ProcedimientoPasoExamenDTO(
        UUID id,
        @Schema(readOnly = true)
        Date fechaCreacion,
        Boolean activo,
        @Size(max = 2000)
        String observaciones,
        UUID idExamen,
        UUID idProcedimientoPaso) {
}
