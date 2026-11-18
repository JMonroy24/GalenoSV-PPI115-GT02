package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record ConsultaDetalleDTO(
        UUID id,
        Date fechaInicio,
        Date fechaFin,
        String referenciaExterna,
        String observaciones,
        UUID idPersonaRol,
        List<ConsultaPasoDTO> pasos) {
}
