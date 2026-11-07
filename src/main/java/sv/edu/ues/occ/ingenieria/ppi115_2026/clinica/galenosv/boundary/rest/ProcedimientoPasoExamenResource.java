package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoPasoExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

@Tag(name = "Exámenes por paso de procedimiento", description = "Exámenes requeridos por cada paso de procedimiento.")
@jakarta.ws.rs.Path("/procedimientos-pasos-examenes")
@ApplicationScoped
public class ProcedimientoPasoExamenResource
        extends GenericResource<ProcedimientoPasoExamen, ProcedimientoPasoExamenDTO, UUID> {

    @Inject
    ProcedimientoPasoExamenService procedimientoPasoExamenService;

    @Inject
    ProcedimientoPasoExamenMapper procedimientoPasoExamenMapper;

    @Override
    protected DefaultService<ProcedimientoPasoExamen, UUID> getService() {
        return procedimientoPasoExamenService;
    }

    @Override
    protected EntityMapper<ProcedimientoPasoExamen, ProcedimientoPasoExamenDTO> getMapper() {
        return procedimientoPasoExamenMapper;
    }

    @Override
    protected UUID getId(ProcedimientoPasoExamenDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ProcedimientoPasoExamen entidad, UUID id) {
        entidad.setIdProcedimientoPasoExamen(id);
    }

    @Override
    protected void asignarNuevoId(ProcedimientoPasoExamen entidad) {
        entidad.setIdProcedimientoPasoExamen(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(ProcedimientoPasoExamen entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
