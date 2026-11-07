package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

@ApplicationScoped
public class ProcedimientoMapper implements EntityMapper<Procedimiento, ProcedimientoDTO> {

    @Override
    public ProcedimientoDTO toDTO(Procedimiento entidad) {
        if (entidad == null) {
            return null;
        }
        return new ProcedimientoDTO(
                entidad.getIdProcedimiento(),
                entidad.getNombre(),
                entidad.getActivo(),
                entidad.getObservaciones());
    }

    @Override
    public Procedimiento toEntity(ProcedimientoDTO dto) {
        if (dto == null) {
            return null;
        }
        Procedimiento entidad = new Procedimiento();
        entidad.setIdProcedimiento(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ProcedimientoDTO dto, Procedimiento destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setActivo(dto.activo());
        destino.setObservaciones(dto.observaciones());
    }
}
