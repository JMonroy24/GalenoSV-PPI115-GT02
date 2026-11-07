package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;

@ApplicationScoped
public class ExamenMapper implements EntityMapper<Examen, ExamenDTO> {

    @Override
    public ExamenDTO toDTO(Examen entidad) {
        if (entidad == null) {
            return null;
        }
        return new ExamenDTO(
                entidad.getIdExamen(),
                entidad.getNombre(),
                entidad.getActivo(),
                entidad.getObservaciones());
    }

    @Override
    public Examen toEntity(ExamenDTO dto) {
        if (dto == null) {
            return null;
        }
        Examen entidad = new Examen();
        entidad.setIdExamen(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ExamenDTO dto, Examen destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setActivo(dto.activo());
        destino.setObservaciones(dto.observaciones());
    }
}
