package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import java.util.Date;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record PersonaRolDTO(
        UUID id,
        @Schema(readOnly = true)
        Date fechaCreacion,
        UUID idClinica,
        UUID idPersona,
        UUID idRol) {
}
