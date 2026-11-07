package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenTipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenTipoExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

@Tag(name = "Exámenes por tipo", description = "Relación entre exámenes y tipos de examen.")
@jakarta.ws.rs.Path("/examenes-tipos-examen")
@ApplicationScoped
public class ExamenTipoExamenResource extends GenericResource<ExamenTipoExamen, ExamenTipoExamenDTO, UUID> {

    @Inject
    ExamenTipoExamenService examenTipoExamenService;

    @Inject
    ExamenTipoExamenMapper examenTipoExamenMapper;

    @Override
    protected DefaultService<ExamenTipoExamen, UUID> getService() {
        return examenTipoExamenService;
    }

    @Override
    protected EntityMapper<ExamenTipoExamen, ExamenTipoExamenDTO> getMapper() {
        return examenTipoExamenMapper;
    }

    @Override
    protected UUID getId(ExamenTipoExamenDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ExamenTipoExamen entidad, UUID id) {
        entidad.setIdExamenTipoExamen(id);
    }

    @Override
    protected void asignarNuevoId(ExamenTipoExamen entidad) {
        entidad.setIdExamenTipoExamen(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
