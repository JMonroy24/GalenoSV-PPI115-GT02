package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record OrdenExamenDTO(
        UUID id,
        @Schema(readOnly = true)
        Date fechaCreacion,
        @Size(max = 2147483647)
        String indicaciones,
        UUID idConsultaProcedimientoPaso,
        UUID idExamen) {

    @JsonbCreator
    public OrdenExamenDTO(
            UUID id,
            Date fechaCreacion,
            String indicaciones,
            UUID idConsultaProcedimientoPaso,
            UUID idExamen) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.indicaciones = indicaciones;
        this.idConsultaProcedimientoPaso = idConsultaProcedimientoPaso;
        this.idExamen = idExamen;
    }

    public OrdenExamenDTO(UUID id, Date fechaCreacion, String indicaciones,
            UUID idConsultaProcedimientoPaso) {
        this(id, fechaCreacion, indicaciones, idConsultaProcedimientoPaso, null);
    }
}
