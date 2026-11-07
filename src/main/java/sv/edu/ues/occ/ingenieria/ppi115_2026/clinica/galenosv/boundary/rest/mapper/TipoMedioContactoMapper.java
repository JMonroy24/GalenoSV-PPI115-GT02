package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoMedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

@ApplicationScoped
public class TipoMedioContactoMapper implements EntityMapper<TipoMedioContacto, TipoMedioContactoDTO> {

    @Override
    public TipoMedioContactoDTO toDTO(TipoMedioContacto entidad) {
        if (entidad == null) {
            return null;
        }
        return new TipoMedioContactoDTO(
                entidad.getIdTipoMedioContacto(),
                entidad.getNombre(),
                entidad.getIndicaciones(),
                entidad.getExpresionRegular(),
                entidad.getActivo());
    }

    @Override
    public TipoMedioContacto toEntity(TipoMedioContactoDTO dto) {
        if (dto == null) {
            return null;
        }
        TipoMedioContacto entidad = new TipoMedioContacto();
        entidad.setIdTipoMedioContacto(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(TipoMedioContactoDTO dto, TipoMedioContacto destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setIndicaciones(dto.indicaciones());
        destino.setExpresionRegular(dto.expresionRegular());
        destino.setActivo(dto.activo());
    }
}
