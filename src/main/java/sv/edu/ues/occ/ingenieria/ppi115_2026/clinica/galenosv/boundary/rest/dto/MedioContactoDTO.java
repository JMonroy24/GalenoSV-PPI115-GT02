package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

public record MedioContactoDTO(
        UUID id,
        @NotBlank
        @Size(max = 2147483647)
        String valor,
        Date fechaCreacion,
        UUID idPersona,
        UUID idTipoMedioContacto) {
}
