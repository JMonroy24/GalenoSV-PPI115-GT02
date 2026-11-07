package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

public record ConsultaProcedimientoPasoDTO(
        UUID id,
        Date fechaInicio,
        Date fechaFin,
        @NotBlank
        @Size(max = 20)
        String estado,
        UUID idConsultaProcedimiento,
        UUID idPersonaRol) {
}
