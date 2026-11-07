package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.TipoDocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.TipoDocumentoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

@Tag(name = "Tipos de documento", description = "Catálogo de tipos de documento.")
@jakarta.ws.rs.Path("/tipos-documento")
@ApplicationScoped
public class TipoDocumentoResource extends GenericResource<TipoDocumento, TipoDocumentoDTO, UUID> {

    @Inject
    TipoDocumentoService tipoDocumentoService;

    @Inject
    TipoDocumentoMapper tipoDocumentoMapper;

    @Override
    protected DefaultService<TipoDocumento, UUID> getService() {
        return tipoDocumentoService;
    }

    @Override
    protected EntityMapper<TipoDocumento, TipoDocumentoDTO> getMapper() {
        return tipoDocumentoMapper;
    }

    @Override
    protected UUID getId(TipoDocumentoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(TipoDocumento entidad, UUID id) {
        entidad.setIdTipoDocumento(id);
    }

    @Override
    protected void asignarNuevoId(TipoDocumento entidad) {
        entidad.setIdTipoDocumento(UUID.randomUUID());
    }

    @Override
    protected boolean borradoLogico() {
        return true;
    }

    @Override
    protected void marcarInactivo(TipoDocumento entidad) {
        entidad.setActivo(Boolean.FALSE);
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
