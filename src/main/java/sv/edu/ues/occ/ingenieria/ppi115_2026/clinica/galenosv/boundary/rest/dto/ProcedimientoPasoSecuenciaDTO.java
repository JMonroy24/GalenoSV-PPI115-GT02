package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ProcedimientoPasoSecuenciaDTO(
        UUID id,
        UUID idProcedimientoPasoReferencia,
        @NotBlank
        @Size(max = 20)
        String tipoSecuencia,
        UUID idProcedimientoPaso) {
}
