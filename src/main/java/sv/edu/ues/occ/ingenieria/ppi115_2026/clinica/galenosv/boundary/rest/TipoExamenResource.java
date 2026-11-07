package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoExamenDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.TipoExamenMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

@Tag(name = "Tipos de examen", description = "Catálogo de tipos de examen.")
@jakarta.ws.rs.Path("/tipos-examen")
@ApplicationScoped
public class TipoExamenResource extends GenericResource<TipoExamen, TipoExamenDTO, UUID> {

    @Inject
    TipoExamenService tipoExamenService;

    @Inject
    TipoExamenMapper tipoExamenMapper;

    @Override
    protected DefaultService<TipoExamen, UUID> getService() {
        return tipoExamenService;
    }

    @Override
    protected EntityMapper<TipoExamen, TipoExamenDTO> getMapper() {
        return tipoExamenMapper;
    }

    @Override
    protected UUID getId(TipoExamenDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(TipoExamen entidad, UUID id) {
        entidad.setIdTipoExamen(id);
    }

    @Override
    protected void asignarNuevoId(TipoExamen entidad) {
        entidad.setIdTipoExamen(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(TipoExamen entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
