package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

@Tag(name = "Procedimientos", description = "Catálogo de procedimientos clínicos.")
@jakarta.ws.rs.Path("/procedimientos")
@ApplicationScoped
public class ProcedimientoResource extends GenericResource<Procedimiento, ProcedimientoDTO, UUID> {

    @Inject
    ProcedimientoService procedimientoService;

    @Inject
    ProcedimientoMapper procedimientoMapper;

    @Override
    protected DefaultService<Procedimiento, UUID> getService() {
        return procedimientoService;
    }

    @Override
    protected EntityMapper<Procedimiento, ProcedimientoDTO> getMapper() {
        return procedimientoMapper;
    }

    @Override
    protected UUID getId(ProcedimientoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(Procedimiento entidad, UUID id) {
        entidad.setIdProcedimiento(id);
    }

    @Override
    protected void asignarNuevoId(Procedimiento entidad) {
        entidad.setIdProcedimiento(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(Procedimiento entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
