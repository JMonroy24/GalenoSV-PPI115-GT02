package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenTipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

@ApplicationScoped
public class ExamenTipoExamenMapper implements EntityMapper<ExamenTipoExamen, ExamenTipoExamenDTO> {

    @Inject
    ExamenService examenService;

    @Inject
    TipoExamenService tipoExamenService;

    @Override
    public ExamenTipoExamenDTO toDTO(ExamenTipoExamen entidad) {
        if (entidad == null) {
            return null;
        }
        return new ExamenTipoExamenDTO(
                entidad.getIdExamenTipoExamen(),
                entidad.getFechaCreacion(),
                entidad.getObservaciones(),
                entidad.getIdExamen() == null ? null : entidad.getIdExamen().getIdExamen(),
                entidad.getIdTipoExamen() == null ? null : entidad.getIdTipoExamen().getIdTipoExamen());
    }

    @Override
    public ExamenTipoExamen toEntity(ExamenTipoExamenDTO dto) {
        if (dto == null) {
            return null;
        }
        ExamenTipoExamen entidad = new ExamenTipoExamen();
        entidad.setIdExamenTipoExamen(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ExamenTipoExamenDTO dto, ExamenTipoExamen destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setObservaciones(dto.observaciones());
        destino.setIdExamen(resolverExamen(dto.idExamen()));
        destino.setIdTipoExamen(resolverTipoExamen(dto.idTipoExamen()));
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

    private TipoExamen resolverTipoExamen(UUID id) {
        if (id == null) {
            return null;
        }
        TipoExamen tipoExamen = tipoExamenService.findById(id);
        if (tipoExamen == null) {
            throw new NotFoundException("No existe el tipo de examen solicitado.");
        }
        return tipoExamen;
    }
}
