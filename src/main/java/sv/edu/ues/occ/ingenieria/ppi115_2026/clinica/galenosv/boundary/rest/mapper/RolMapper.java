package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.RolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

@ApplicationScoped
public class RolMapper implements EntityMapper<Rol, RolDTO> {

    @Override
    public RolDTO toDTO(Rol entidad) {
        if (entidad == null) {
            return null;
        }
        return new RolDTO(
                entidad.getIdRol(),
                entidad.getNombre(),
                entidad.getActivo(),
                entidad.getObservaciones());
    }

    @Override
    public Rol toEntity(RolDTO dto) {
        if (dto == null) {
            return null;
        }
        Rol entidad = new Rol();
        entidad.setIdRol(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(RolDTO dto, Rol destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setActivo(dto.activo());
        destino.setObservaciones(dto.observaciones());
    }
}
