package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.ExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.ExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;

@Tag(name = "Exámenes", description = "Catálogo de exámenes.")
@jakarta.ws.rs.Path("/examenes")
@ApplicationScoped
public class ExamenResource extends GenericResource<Examen, ExamenDTO, UUID> {

    @Inject
    ExamenService examenService;

    @Inject
    ExamenMapper examenMapper;

    @Override
    protected DefaultService<Examen, UUID> getService() {
        return examenService;
    }

    @Override
    protected EntityMapper<Examen, ExamenDTO> getMapper() {
        return examenMapper;
    }

    @Override
    protected UUID getId(ExamenDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(Examen entidad, UUID id) {
        entidad.setIdExamen(id);
    }

    @Override
    protected void asignarNuevoId(Examen entidad) {
        entidad.setIdExamen(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(Examen entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
