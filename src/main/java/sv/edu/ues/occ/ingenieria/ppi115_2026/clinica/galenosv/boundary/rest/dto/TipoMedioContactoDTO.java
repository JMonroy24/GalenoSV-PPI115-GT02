package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record TipoMedioContactoDTO(
        UUID id,
        @NotBlank
        @Size(max = 155)
        String nombre,
        @Size(max = 2000)
        String indicaciones,
        @Size(max = 500)
        String expresionRegular,
        Boolean activo) {
}
