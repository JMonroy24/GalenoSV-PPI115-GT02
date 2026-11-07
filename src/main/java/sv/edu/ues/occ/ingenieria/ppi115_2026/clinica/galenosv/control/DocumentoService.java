package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

@ApplicationScoped
public class DocumentoService extends DefaultService<Documento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected DocumentoDAO documentoDAO;

    public DocumentoService() {
    }

    public DocumentoService(DocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
    }

    @Override
    protected DefaultDAO<Documento, UUID> getDAO() {
        return documentoDAO;
    }

    @Override
    protected void validar(Documento r, boolean nuevo) {
        r.setValor(ValidadorComun.textoObligatorio(r.getValor(), "El documento"));
        var persona = ValidadorComun.requerido(r.getIdPersona(), "Seleccione una persona.");
        var tipo = ValidadorComun.requerido(r.getIdTipoDocumento(), "Seleccione un tipo de documento.");
        ValidadorComun.requerido(tipo.getIdTipoDocumento(), "Seleccione un tipo de documento válido.");
        ValidadorComun.activo(tipo.getActivo(), "El tipo de documento");
        ValidadorComun.formato(r.getValor(), tipo.getExpresionRegular(), tipo.getIndicaciones());
        if (documentoDAO.existePersonaTipoValor(persona.getIdPersona(), tipo.getIdTipoDocumento(),
                r.getValor(), r.getIdDocumento())) {
            throw new ValidacionNegocioException("La persona ya tiene un documento de este tipo con el mismo valor.");
        }
    }

    public boolean existeTipoValor(UUID idTipo, String valor, UUID excluirId) {
        return documentoDAO.existeTipoValor(idTipo, valor, excluirId);
    }

    public boolean existePersonaTipoValor(UUID idPersona, UUID idTipo, String valor, UUID excluirId) {
        return documentoDAO.existePersonaTipoValor(idPersona, idTipo, valor, excluirId);
    }

    public List<Documento> findByPersona(UUID idPersona) {
        if (idPersona == null) {
            return List.of();
        }
        return documentoDAO.findByPersona(idPersona);
    }

    public DocumentoDAO getDocumentoDAO() {
        return documentoDAO;
    }

    public void setDocumentoDAO(DocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
    }
}
