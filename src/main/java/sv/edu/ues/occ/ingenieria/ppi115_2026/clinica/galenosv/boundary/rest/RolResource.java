package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.RolDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.RolMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

@Tag(name = "Roles", description = "Roles disponibles para el personal.")
@jakarta.ws.rs.Path("/roles")
@ApplicationScoped
public class RolResource extends GenericResource<Rol, RolDTO, UUID> {

    @Inject
    RolService rolService;

    @Inject
    RolMapper rolMapper;

    @Override
    protected DefaultService<Rol, UUID> getService() {
        return rolService;
    }

    @Override
    protected EntityMapper<Rol, RolDTO> getMapper() {
        return rolMapper;
    }

    @Override
    protected UUID getId(RolDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(Rol entidad, UUID id) {
        entidad.setIdRol(id);
    }

    @Override
    protected void asignarNuevoId(Rol entidad) {
        entidad.setIdRol(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(Rol entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
