package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Estado del paso y formulario serializado como una cadena JSON. */
public record ConsultaProcedimientoPasoActualizacionDTO(
        @NotBlank @Size(max = 20) String estadoPaso,
        @NotNull String valor) {
}
