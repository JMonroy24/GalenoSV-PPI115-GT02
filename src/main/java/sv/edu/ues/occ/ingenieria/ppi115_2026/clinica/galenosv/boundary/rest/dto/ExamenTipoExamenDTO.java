package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

public record ExamenTipoExamenDTO(
        UUID id,
        Date fechaCreacion,
        @Size(max = 2147483647)
        String observaciones,
        UUID idExamen,
        UUID idTipoExamen) {
}
