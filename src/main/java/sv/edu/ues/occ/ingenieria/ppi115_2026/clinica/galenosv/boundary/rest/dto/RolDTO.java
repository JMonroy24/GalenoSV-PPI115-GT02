package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RolDTO(
        UUID id,
        @NotBlank
        @Size(max = 155)
        String nombre,
        Boolean activo,
        @Size(max = 2000)
        String observaciones) {
}
