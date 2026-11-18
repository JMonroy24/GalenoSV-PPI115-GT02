package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import java.util.UUID;

public record ConsultaPasoDTO(
        UUID id,
        String nombre,
        Integer orden,
        String estado,
        String valor,
        UUID idProcedimientoPaso) {
}
