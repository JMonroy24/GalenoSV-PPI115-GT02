package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

@ApplicationScoped
public class ConsultaProcedimientoMapper
        implements EntityMapper<ConsultaProcedimiento, ConsultaProcedimientoDTO> {

    @Inject
    ConsultaService consultaService;

    @Inject
    ProcedimientoService procedimientoService;

    @Override
    public ConsultaProcedimientoDTO toDTO(ConsultaProcedimiento entidad) {
        if (entidad == null) {
            return null;
        }
        return new ConsultaProcedimientoDTO(
                entidad.getIdConsultaProcedimiento(),
                entidad.getFechaInicio(),
                entidad.getFechaFin(),
                entidad.getObservaciones(),
                entidad.getIdConsulta() == null ? null : entidad.getIdConsulta().getIdConsulta(),
                entidad.getIdProcedimiento() == null ? null : entidad.getIdProcedimiento().getIdProcedimiento());
    }

    @Override
    public ConsultaProcedimiento toEntity(ConsultaProcedimientoDTO dto) {
        if (dto == null) {
            return null;
        }
        ConsultaProcedimiento entidad = new ConsultaProcedimiento();
        entidad.setIdConsultaProcedimiento(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(ConsultaProcedimientoDTO dto, ConsultaProcedimiento destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setFechaInicio(dto.fechaInicio());
        destino.setFechaFin(dto.fechaFin());
        destino.setObservaciones(dto.observaciones());
        destino.setIdConsulta(resolverConsulta(dto.idConsulta()));
        destino.setIdProcedimiento(resolverProcedimiento(dto.idProcedimiento()));
    }

    private Consulta resolverConsulta(UUID id) {
        if (id == null) {
            return null;
        }
        Consulta consulta = consultaService.findById(id);
        if (consulta == null) {
            throw new NotFoundException("No existe la consulta solicitada.");
        }
        return consulta;
    }

    private Procedimiento resolverProcedimiento(UUID id) {
        if (id == null) {
            return null;
        }
        Procedimiento procedimiento = procedimientoService.findById(id);
        if (procedimiento == null) {
            throw new NotFoundException("No existe el procedimiento solicitado.");
        }
        return procedimiento;
    }
}
