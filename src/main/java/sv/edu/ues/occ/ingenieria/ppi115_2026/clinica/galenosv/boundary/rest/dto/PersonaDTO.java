package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record PersonaDTO(
        UUID id,
        @NotBlank
        @Size(max = 255)
        String nombres,
        @NotBlank
        @Size(max = 255)
        String apellidos,
        Date fechaNacimiento,
        @Schema(readOnly = true)
        Date fechaCreacion) {
}
