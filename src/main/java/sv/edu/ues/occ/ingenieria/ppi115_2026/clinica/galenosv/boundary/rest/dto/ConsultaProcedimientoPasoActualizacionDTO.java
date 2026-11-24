package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Estado del paso y formulario serializado como una cadena JSON. */
public record ConsultaProcedimientoPasoActualizacionDTO(
        @NotBlank @Size(max = 20) String estadoPaso,
        @NotNull String valor,
        @jakarta.validation.constraints.Pattern(regexp = "RETROCEDER") String accion) {
    @jakarta.json.bind.annotation.JsonbCreator
    public ConsultaProcedimientoPasoActualizacionDTO(String estadoPaso, String valor, String accion) {
        this.estadoPaso = estadoPaso; this.valor = valor; this.accion = accion;
    }

    public ConsultaProcedimientoPasoActualizacionDTO(String estadoPaso, String valor) {
        this(estadoPaso, valor, null);
    }
}
