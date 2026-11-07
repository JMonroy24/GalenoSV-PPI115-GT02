package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoMedioContactoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.TipoMedioContactoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

@Tag(name = "Tipos de medio de contacto", description = "Catálogo de tipos de medio de contacto.")
@jakarta.ws.rs.Path("/tipos-medio-contacto")
@ApplicationScoped
public class TipoMedioContactoResource extends GenericResource<TipoMedioContacto, TipoMedioContactoDTO, UUID> {

    @Inject
    TipoMedioContactoService tipoMedioContactoService;

    @Inject
    TipoMedioContactoMapper tipoMedioContactoMapper;

    @Override
    protected DefaultService<TipoMedioContacto, UUID> getService() {
        return tipoMedioContactoService;
    }

    @Override
    protected EntityMapper<TipoMedioContacto, TipoMedioContactoDTO> getMapper() {
        return tipoMedioContactoMapper;
    }

    @Override
    protected UUID getId(TipoMedioContactoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(TipoMedioContacto entidad, UUID id) {
        entidad.setIdTipoMedioContacto(id);
    }

    @Override
    protected void asignarNuevoId(TipoMedioContacto entidad) {
        entidad.setIdTipoMedioContacto(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(TipoMedioContacto entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
