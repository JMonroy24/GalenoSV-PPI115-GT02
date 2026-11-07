package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenResultadoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenResultadoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;

@Tag(name = "Resultados de exámenes", description = "Resultados asociados a órdenes de examen.")
@jakarta.ws.rs.Path("/examenes-resultados")
@ApplicationScoped
public class ExamenResultadoResource extends GenericResource<ExamenResultado, ExamenResultadoDTO, UUID> {

    @Inject
    ExamenResultadoService examenResultadoService;

    @Inject
    ExamenResultadoMapper examenResultadoMapper;

    @Override
    protected DefaultService<ExamenResultado, UUID> getService() {
        return examenResultadoService;
    }

    @Override
    protected EntityMapper<ExamenResultado, ExamenResultadoDTO> getMapper() {
        return examenResultadoMapper;
    }

    @Override
    protected UUID getId(ExamenResultadoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(ExamenResultado entidad, UUID id) {
        entidad.setIdExamenResultado(id);
    }

    @Override
    protected void asignarNuevoId(ExamenResultado entidad) {
        entidad.setIdExamenResultado(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
