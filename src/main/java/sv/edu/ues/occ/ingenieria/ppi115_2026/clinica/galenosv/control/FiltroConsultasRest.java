package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.UUID;

/** Parámetros tipados compartidos por el listado y su conteo. */
public record FiltroConsultasRest(UUID pacienteId, UUID idClinica, UUID idRol,
        Date desde, Date hasta, String q) {
}
