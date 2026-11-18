package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenAsignableDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

@ApplicationScoped
public class ExamenAsignableMapper {

    @Inject
    ExamenService examenService;

    public ExamenAsignableDTO toDTO(ProcedimientoPasoExamen relacion) {
        if (relacion == null) {
            return null;
        }
        Examen examen = relacion.getIdExamen();
        UUID idExamen = examen == null ? null : examen.getIdExamen();
        UUID idTipoExamen = null;
        String nombreTipoExamen = null;
        List<ExamenTipoExamen> tipos = examenService.findTiposByExamen(idExamen);
        if (tipos != null && !tipos.isEmpty() && tipos.get(0).getIdTipoExamen() != null) {
            idTipoExamen = tipos.get(0).getIdTipoExamen().getIdTipoExamen();
            nombreTipoExamen = tipos.get(0).getIdTipoExamen().getNombre();
        }
        return new ExamenAsignableDTO(
                relacion.getIdProcedimientoPasoExamen(),
                idExamen,
                examen == null ? null : examen.getNombre(),
                relacion.getActivo(),
                relacion.getIdProcedimientoPaso() == null
                        ? null : relacion.getIdProcedimientoPaso().getIdProcedimientoPaso(),
                idTipoExamen,
                nombreTipoExamen);
    }
}
