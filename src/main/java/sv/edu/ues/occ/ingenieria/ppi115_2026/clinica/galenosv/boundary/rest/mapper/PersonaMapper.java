package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.PersonaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

@ApplicationScoped
public class PersonaMapper implements EntityMapper<Persona, PersonaDTO> {

    @Override
    public PersonaDTO toDTO(Persona entidad) {
        if (entidad == null) {
            return null;
        }
        return new PersonaDTO(
                entidad.getIdPersona(),
                entidad.getNombres(),
                entidad.getApellidos(),
                entidad.getFechaNacimiento(),
                entidad.getFechaCreacion());
    }

    @Override
    public Persona toEntity(PersonaDTO dto) {
        if (dto == null) {
            return null;
        }
        Persona entidad = new Persona();
        entidad.setIdPersona(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(PersonaDTO dto, Persona destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setNombres(dto.nombres());
        destino.setApellidos(dto.apellidos());
        destino.setFechaNacimiento(dto.fechaNacimiento());
    }
}
