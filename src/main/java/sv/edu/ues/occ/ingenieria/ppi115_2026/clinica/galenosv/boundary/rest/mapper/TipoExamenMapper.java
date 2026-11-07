package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

@ApplicationScoped
public class TipoExamenMapper implements EntityMapper<TipoExamen, TipoExamenDTO> {

    @Override
    public TipoExamenDTO toDTO(TipoExamen entidad) {
        if (entidad == null) {
            return null;
        }
        return new TipoExamenDTO(
                entidad.getIdTipoExamen(),
                entidad.getNombre(),
                entidad.getActivo(),
                entidad.getObservaciones());
    }

    @Override
    public TipoExamen toEntity(TipoExamenDTO dto) {
        if (dto == null) {
            return null;
        }
        TipoExamen entidad = new TipoExamen();
        entidad.setIdTipoExamen(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(TipoExamenDTO dto, TipoExamen destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setActivo(dto.activo());
        destino.setObservaciones(dto.observaciones());
    }
}
