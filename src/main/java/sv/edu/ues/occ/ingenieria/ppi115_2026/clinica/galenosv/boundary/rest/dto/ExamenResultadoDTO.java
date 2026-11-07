package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

public record ExamenResultadoDTO(
        UUID id,
        Date fechaCreacion,
        @NotBlank
        @Size(max = 2147483647)
        String resultado,
        @NotBlank
        @Size(max = 2147483647)
        String interpretacion,
        @Size(max = 2147483647)
        String rutaAtestado,
        UUID idOrdenExamen) {
}
