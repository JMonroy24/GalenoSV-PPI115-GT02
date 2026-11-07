package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ProcedimientoPasoSecuenciaDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ProcedimientoPasoSecuenciaMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

@Tag(name = "Secuencias de pasos", description = "Orden de ejecución de los pasos de procedimiento.")
@jakarta.ws.rs.Path("/procedimientos-pasos-secuencias")
@ApplicationScoped
public class ProcedimientoPasoSecuenciaResource
        extends GenericResource<ProcedimientoPasoSecuencia, ProcedimientoPasoSecuenciaDTO, UUID> {

    @Inject
    ProcedimientoPasoSecuenciaService procedimientoPasoSecuenciaService;

    @Inject
    ProcedimientoPasoSecuenciaMapper procedimientoPasoSecuenciaMapper;

    @Override
    protected DefaultService<ProcedimientoPasoSecuencia, UUID> getService() {
        return procedimientoPasoSecuenciaService;
    }

    @Override
    protected EntityMapper<ProcedimientoPasoSecuencia, ProcedimientoPasoSecuenciaDTO> getMapper() {
        return procedimientoPasoSecuenciaMapper;
    }

    @Override
    protected UUID getId(ProcedimientoPasoSecuenciaDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ProcedimientoPasoSecuencia entidad, UUID id) {
        entidad.setIdProcedimientoPasoSecuencia(id);
    }

    @Override
    protected void asignarNuevoId(ProcedimientoPasoSecuencia entidad) {
        entidad.setIdProcedimientoPasoSecuencia(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
