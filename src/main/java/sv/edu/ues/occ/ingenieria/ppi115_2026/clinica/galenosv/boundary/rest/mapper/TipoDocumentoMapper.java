package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoDocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

@ApplicationScoped
public class TipoDocumentoMapper implements EntityMapper<TipoDocumento, TipoDocumentoDTO> {

    @Override
    public TipoDocumentoDTO toDTO(TipoDocumento entidad) {
        if (entidad == null) {
            return null;
        }
        return new TipoDocumentoDTO(
                entidad.getIdTipoDocumento(),
                entidad.getNombre(),
                entidad.getIndicaciones(),
                entidad.getExpresionRegular(),
                entidad.getActivo());
    }

    @Override
    public TipoDocumento toEntity(TipoDocumentoDTO dto) {
        if (dto == null) {
            return null;
        }
        TipoDocumento entidad = new TipoDocumento();
        entidad.setIdTipoDocumento(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(TipoDocumentoDTO dto, TipoDocumento destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setIndicaciones(dto.indicaciones());
        destino.setExpresionRegular(dto.expresionRegular());
        destino.setActivo(dto.activo());
    }
}
