package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.DocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper.DocumentoMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DefaultService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

@Tag(name = "Documentos", description = "Documentos de personas.")
@jakarta.ws.rs.Path("/documentos")
@ApplicationScoped
public class DocumentoResource extends GenericResource<Documento, DocumentoDTO, UUID> {

    @Inject
    DocumentoService documentoService;

    @Inject
    DocumentoMapper documentoMapper;

    @Override
    protected DefaultService<Documento, UUID> getService() {
        return documentoService;
    }

    @Override
    protected EntityMapper<Documento, DocumentoDTO> getMapper() {
        return documentoMapper;
    }

    @Override
    protected UUID getId(DocumentoDTO dto) {
        return dto == null ? null : dto.id();
    }

    @Override
    protected void setId(Documento entidad, UUID id) {
        entidad.setIdDocumento(id);
    }

    @Override
    protected void asignarNuevoId(Documento entidad) {
        entidad.setIdDocumento(UUID.randomUUID());
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }
}
