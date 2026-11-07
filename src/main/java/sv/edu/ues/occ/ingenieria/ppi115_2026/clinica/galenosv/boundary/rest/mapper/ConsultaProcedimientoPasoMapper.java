package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

@ApplicationScoped
public class ConsultaProcedimientoPasoMapper
        implements EntityMapper<ConsultaProcedimientoPaso, ConsultaProcedimientoPasoDTO> {

    @Inject
    ConsultaProcedimientoService consultaProcedimientoService;

    @Inject
    PersonaRolService personaRolService;

    @Override
    public ConsultaProcedimientoPasoDTO toDTO(ConsultaProcedimientoPaso entidad) {
        if (entidad == null) {
            return null;
        }
        return new ConsultaProcedimientoPasoDTO(
                entidad.getIdConsultaProcedimientoPaso(),
                entidad.getFechaInicio(),
                entidad.getFechaFin(),
                entidad.getEstado(),
                entidad.getIdConsultaProcedimiento() == null
                        ? null
                        : entidad.getIdConsultaProcedimiento().getIdConsultaProcedimiento(),
                entidad.getIdPersonaRol() == null ? null : entidad.getIdPersonaRol().getIdPersonaRol());
    }

    @Override
    public ConsultaProcedimientoPaso toEntity(ConsultaProcedimientoPasoDTO dto) {
        if (dto == null) {
            return null;
        }
        ConsultaProcedimientoPaso entidad = new ConsultaProcedimientoPaso();
        entidad.setIdConsultaProcedimientoPaso(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ConsultaProcedimientoPasoDTO dto, ConsultaProcedimientoPaso destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setFechaInicio(dto.fechaInicio());
        destino.setFechaFin(dto.fechaFin());
        destino.setEstado(dto.estado());
        destino.setIdConsultaProcedimiento(resolverConsultaProcedimiento(dto.idConsultaProcedimiento()));
        destino.setIdPersonaRol(resolverPersonaRol(dto.idPersonaRol()));
    }

    private ConsultaProcedimiento resolverConsultaProcedimiento(UUID id) {
        if (id == null) {
            return null;
        }
        ConsultaProcedimiento consultaProcedimiento = consultaProcedimientoService.findById(id);
        if (consultaProcedimiento == null) {
            throw new NotFoundException("No existe el procedimiento de consulta solicitado.");
        }
        return consultaProcedimiento;
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
