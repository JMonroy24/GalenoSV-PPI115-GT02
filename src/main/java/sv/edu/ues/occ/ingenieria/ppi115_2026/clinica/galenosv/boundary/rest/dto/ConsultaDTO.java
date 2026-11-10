package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

public record ConsultaDTO(
        UUID id,
        Date fechaInicio,
        Date fechaFin,
        @Size(max = 255)
        String referenciaExterna,
        @Size(max = 2000)
        String observaciones,
        UUID idPersonaRol) {
}
