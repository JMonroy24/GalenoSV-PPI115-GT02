package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ClinicaDTO(
        UUID id,
        @NotBlank
        @Size(max = 255)
        String nombre,
        Boolean activo,
        @NotBlank
        @Size(max = 20)
        String tipo,
        @Size(max = 2147483647)
        String comentarios) {
}
