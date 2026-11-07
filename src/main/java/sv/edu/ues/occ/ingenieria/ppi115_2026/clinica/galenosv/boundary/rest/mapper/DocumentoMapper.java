package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.EntityMapper;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.dto.DocumentoDTO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

@ApplicationScoped
public class DocumentoMapper implements EntityMapper<Documento, DocumentoDTO> {

    @Inject
    PersonaService personaService;

    @Inject
    TipoDocumentoService tipoDocumentoService;

    @Override
    public DocumentoDTO toDTO(Documento entidad) {
        if (entidad == null) {
            return null;
        }
        return new DocumentoDTO(
                entidad.getIdDocumento(),
                entidad.getValor(),
                entidad.getIdPersona() == null ? null : entidad.getIdPersona().getIdPersona(),
                entidad.getIdTipoDocumento() == null ? null : entidad.getIdTipoDocumento().getIdTipoDocumento());
    }

    @Override
    public Documento toEntity(DocumentoDTO dto) {
        if (dto == null) {
            return null;
        }
        Documento entidad = new Documento();
        entidad.setIdDocumento(dto.id());
        copiar(dto, entidad);
        return entidad;
    }

    @Override
    public void copiar(DocumentoDTO dto, Documento destino) {
        if (dto == null || destino == null) {
            return;
        }
        destino.setValor(dto.valor());
        destino.setIdPersona(resolverPersona(dto.idPersona()));
        destino.setIdTipoDocumento(resolverTipoDocumento(dto.idTipoDocumento()));
    }

    private Persona resolverPersona(UUID id) {
        if (id == null) {
            return null;
        }
        Persona persona = personaService.findById(id);
        if (persona == null) {
            throw new NotFoundException("No existe la persona solicitada.");
        }
        return persona;
    }

    private TipoDocumento resolverTipoDocumento(UUID id) {
        if (id == null) {
            return null;
        }
        TipoDocumento tipo = tipoDocumentoService.findById(id);
        if (tipo == null) {
            throw new NotFoundException("No existe el tipo de documento solicitado.");
        }
        return tipo;
    }
}
