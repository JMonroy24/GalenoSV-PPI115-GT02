package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ClinicaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

@ApplicationScoped
public class ClinicaMapper implements EntityMapper<Clinica, ClinicaDTO> {

    @Override
    public ClinicaDTO toDTO(Clinica entidad) {
        if (entidad == null) {
            return null;
        }
        return new ClinicaDTO(
                entidad.getIdClinica(),
                entidad.getNombre(),
                entidad.getActivo(),
                entidad.getTipo(),
                entidad.getComentarios());
    }

    @Override
    public Clinica toEntity(ClinicaDTO dto) {
        if (dto == null) {
            return null;
        }
        Clinica entidad = new Clinica();
        entidad.setIdClinica(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ClinicaDTO dto, Clinica destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombre(dto.nombre());
        destino.setActivo(dto.activo());
        destino.setTipo(dto.tipo());
        destino.setComentarios(dto.comentarios());
    }
}
