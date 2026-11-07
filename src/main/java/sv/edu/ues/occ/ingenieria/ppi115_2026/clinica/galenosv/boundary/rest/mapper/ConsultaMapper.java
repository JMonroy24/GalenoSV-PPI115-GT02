package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

@ApplicationScoped
public class ConsultaMapper implements EntityMapper<Consulta, ConsultaDTO> {

    @Inject
    PersonaRolService personaRolService;

    @Override
    public ConsultaDTO toDTO(Consulta entidad) {
        if (entidad == null) {
            return null;
        }
        return new ConsultaDTO(
                entidad.getIdConsulta(),
                entidad.getFechaInicio(),
                entidad.getFechaFin(),
                entidad.getReferenciaExterna(),
                entidad.getObservaciones(),
                entidad.getIdPersonaRol() == null ? null : entidad.getIdPersonaRol().getIdPersonaRol());
    }

    @Override
    public Consulta toEntity(ConsultaDTO dto) {
        if (dto == null) {
            return null;
        }
        Consulta entidad = new Consulta();
        entidad.setIdConsulta(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ConsultaDTO dto, Consulta destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setFechaInicio(dto.fechaInicio());
        destino.setFechaFin(dto.fechaFin());
        destino.setReferenciaExterna(dto.referenciaExterna());
        destino.setObservaciones(dto.observaciones());
        destino.setIdPersonaRol(resolverPersonaRol(dto.idPersonaRol()));
    }

    private PersonaRol resolverPersonaRol(UUID id) {
        if (id == null) {
            return null;
        }
        PersonaRol personaRol = personaRolService.findById(id);
        if (personaRol == null) {
            throw new NotFoundException("No existe la persona-rol solicitada.");
        }
        return personaRol;
    }
}
