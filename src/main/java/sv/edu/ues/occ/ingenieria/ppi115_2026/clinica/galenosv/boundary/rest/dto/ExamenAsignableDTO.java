package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import java.util.UUID;

public record ExamenAsignableDTO(
        UUID idProcedimientoPasoExamen,
        UUID idExamen,
        String nombre,
        Boolean activo,
        UUID idProcedimientoPaso,
        UUID idTipoExamen,
        String nombreTipoExamen) {
}
