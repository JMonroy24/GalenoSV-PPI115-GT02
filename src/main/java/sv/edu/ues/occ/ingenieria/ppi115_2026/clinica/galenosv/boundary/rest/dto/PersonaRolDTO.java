package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import java.util.Date;
import java.util.UUID;

public record PersonaRolDTO(
        UUID id,
        Date fechaCreacion,
        UUID idClinica,
        UUID idPersona,
        UUID idRol) {
}
