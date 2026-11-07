package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.OrdenExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.OrdenExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

@Tag(name = "Órdenes de examen", description = "Órdenes de examen generadas en consultas.")
@jakarta.ws.rs.Path("/ordenes-examen")
@ApplicationScoped
public class OrdenExamenResource extends GenericResource<OrdenExamen, OrdenExamenDTO, UUID> {

    @Inject
    OrdenExamenService ordenExamenService;

    @Inject
    OrdenExamenMapper ordenExamenMapper;

    @Override
    protected DefaultService<OrdenExamen, UUID> getService() {
        return ordenExamenService;
    }

    @Override
    protected EntityMapper<OrdenExamen, OrdenExamenDTO> getMapper() {
        return ordenExamenMapper;
    }

    @Override
    protected UUID getId(OrdenExamenDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(OrdenExamen entidad, UUID id) {
        entidad.setIdOrdenExamen(id);
    }

    @Override
    protected void asignarNuevoId(OrdenExamen entidad) {
        entidad.setIdOrdenExamen(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
