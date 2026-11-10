package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record ExamenResultadoDTO(
        UUID id,
        @Schema(readOnly = true)
        Date fechaCreacion,
        @NotBlank
        @Size(max = 4000)
        String resultado,
        @NotBlank
        @Size(max = 4000)
        String interpretacion,
        @Size(max = 500)
        String rutaAtestado,
        UUID idOrdenExamen) {
}
