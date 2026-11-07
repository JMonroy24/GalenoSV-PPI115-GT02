package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ConsultaProcedimientoPasoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ConsultaProcedimientoPasoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

@Tag(name = "Consultas procedimientos pasos", description = "Pasos ejecutados de un procedimiento dentro de una consulta.")
@jakarta.ws.rs.Path("/consultas-procedimientos-pasos")
@ApplicationScoped
public class ConsultaProcedimientoPasoResource
        extends GenericResource<ConsultaProcedimientoPaso, ConsultaProcedimientoPasoDTO, UUID> {

    @Inject
    ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @Inject
    ConsultaProcedimientoPasoMapper consultaProcedimientoPasoMapper;

    @Override
    protected DefaultService<ConsultaProcedimientoPaso, UUID> getService() {
        return consultaProcedimientoPasoService;
    }

    @Override
    protected EntityMapper<ConsultaProcedimientoPaso, ConsultaProcedimientoPasoDTO> getMapper() {
        return consultaProcedimientoPasoMapper;
    }

    @Override
    protected UUID getId(ConsultaProcedimientoPasoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ConsultaProcedimientoPaso entidad, UUID id) {
        entidad.setIdConsultaProcedimientoPaso(id);
    }

    @Override
    protected void asignarNuevoId(ConsultaProcedimientoPaso entidad) {
        entidad.setIdConsultaProcedimientoPaso(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
