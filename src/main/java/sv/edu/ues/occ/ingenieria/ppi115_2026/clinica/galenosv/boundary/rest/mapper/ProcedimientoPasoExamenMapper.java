package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

@ApplicationScoped
public class ProcedimientoPasoExamenMapper
        implements EntityMapper<ProcedimientoPasoExamen, ProcedimientoPasoExamenDTO> {

    @Inject
    ExamenService examenService;

    @Inject
    ProcedimientoPasoService procedimientoPasoService;

    @Override
    public ProcedimientoPasoExamenDTO toDTO(ProcedimientoPasoExamen entidad) {
        if (entidad == null) {
            return null;
        }
        return new ProcedimientoPasoExamenDTO(
                entidad.getIdProcedimientoPasoExamen(),
                entidad.getFechaCreacion(),
                entidad.getActivo(),
                entidad.getObservaciones(),
                entidad.getIdExamen() == null ? null : entidad.getIdExamen().getIdExamen(),
                entidad.getIdProcedimientoPaso() == null
                        ? null
                        : entidad.getIdProcedimientoPaso().getIdProcedimientoPaso());
    }

    @Override
    public ProcedimientoPasoExamen toEntity(ProcedimientoPasoExamenDTO dto) {
        if (dto == null) {
            return null;
        }
        ProcedimientoPasoExamen entidad = new ProcedimientoPasoExamen();
        entidad.setIdProcedimientoPasoExamen(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ProcedimientoPasoExamenDTO dto, ProcedimientoPasoExamen destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setActivo(dto.activo());
        destino.setObservaciones(dto.observaciones());
        destino.setIdExamen(resolverExamen(dto.idExamen()));
        destino.setIdProcedimientoPaso(resolverPaso(dto.idProcedimientoPaso()));
    }

    private Examen resolverExamen(UUID id) {
        if (id == null) {
            return null;
        }
        Examen examen = examenService.findById(id);
        if (examen == null) {
            throw new NotFoundException("No existe el examen solicitado.");
        }
        return examen;
    }

    private ProcedimientoPaso resolverPaso(UUID id) {
        if (id == null) {
            return null;
        }
        ProcedimientoPaso paso = procedimientoPasoService.findById(id);
        if (paso == null) {
            throw new NotFoundException("No existe el paso de procedimiento solicitado.");
        }
        return paso;
    }
}
