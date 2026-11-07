package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaProcedimientoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

@Tag(name = "Consultas procedimientos", description = "Procedimientos asociados a una consulta.")
@jakarta.ws.rs.Path("/consultas-procedimientos")
@ApplicationScoped
public class ConsultaProcedimientoResource
        extends GenericResource<ConsultaProcedimiento, ConsultaProcedimientoDTO, UUID> {

    @Inject
    ConsultaProcedimientoService consultaProcedimientoService;

    @Inject
    ConsultaProcedimientoMapper consultaProcedimientoMapper;

    @Override
    protected DefaultService<ConsultaProcedimiento, UUID> getService() {
        return consultaProcedimientoService;
    }

    @Override
    protected EntityMapper<ConsultaProcedimiento, ConsultaProcedimientoDTO> getMapper() {
        return consultaProcedimientoMapper;
    }

    @Override
    protected UUID getId(ConsultaProcedimientoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ConsultaProcedimiento entidad, UUID id) {
        entidad.setIdConsultaProcedimiento(id);
    }

    @Override
    protected void asignarNuevoId(ConsultaProcedimiento entidad) {
        entidad.setIdConsultaProcedimiento(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
