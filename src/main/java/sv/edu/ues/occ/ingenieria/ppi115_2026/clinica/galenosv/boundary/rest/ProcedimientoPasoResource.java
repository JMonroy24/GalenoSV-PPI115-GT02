package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoPasoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

@Tag(name = "Pasos de procedimiento", description = "Pasos que componen un procedimiento.")
@jakarta.ws.rs.Path("/procedimientos-pasos")
@ApplicationScoped
public class ProcedimientoPasoResource extends GenericResource<ProcedimientoPaso, ProcedimientoPasoDTO, UUID> {

    @Inject
    ProcedimientoPasoService procedimientoPasoService;

    @Inject
    ProcedimientoPasoMapper procedimientoPasoMapper;

    @Override
    protected DefaultService<ProcedimientoPaso, UUID> getService() {
        return procedimientoPasoService;
    }

    @Override
    protected EntityMapper<ProcedimientoPaso, ProcedimientoPasoDTO> getMapper() {
        return procedimientoPasoMapper;
    }

    @Override
    protected UUID getId(ProcedimientoPasoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ProcedimientoPaso entidad, UUID id) {
        entidad.setIdProcedimientoPaso(id);
    }

    @Override
    protected void asignarNuevoId(ProcedimientoPaso entidad) {
        entidad.setIdProcedimientoPaso(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
