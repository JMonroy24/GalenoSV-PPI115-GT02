package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record OrdenExamenDTO(
        UUID id,
        @Schema(readOnly = true)
        Date fechaCreacion,
        @NotBlank
        @Size(max = 2000)
        String indicaciones,
        UUID idConsultaProcedimientoPaso) {
}
