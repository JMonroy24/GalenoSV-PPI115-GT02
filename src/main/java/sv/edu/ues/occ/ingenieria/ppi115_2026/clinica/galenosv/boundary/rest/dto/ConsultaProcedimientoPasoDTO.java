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
        UUID idPersonaRol,
        String valor,
        UUID idProcedimientoPaso) {

    @jakarta.json.bind.annotation.JsonbCreator
    public ConsultaProcedimientoPasoDTO(UUID id, Date fechaInicio, Date fechaFin, String estado,
            UUID idConsultaProcedimiento, UUID idPersonaRol, String valor, UUID idProcedimientoPaso) {
        this.id = id; this.fechaInicio = fechaInicio; this.fechaFin = fechaFin; this.estado = estado;
        this.idConsultaProcedimiento = idConsultaProcedimiento; this.idPersonaRol = idPersonaRol;
        this.valor = valor; this.idProcedimientoPaso = idProcedimientoPaso;
    }

    public ConsultaProcedimientoPasoDTO(UUID id, Date fechaInicio, Date fechaFin, String estado,
            UUID idConsultaProcedimiento, UUID idPersonaRol, String valor) {
        this(id, fechaInicio, fechaFin, estado, idConsultaProcedimiento, idPersonaRol, valor, null);
    }
}
