package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

/**
 * Backing bean JSF para la gestión de la entidad Documento.
 */
@Named("documentoModel")
@ViewScoped
public class DocumentoModel extends ModelTransaccional<Documento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected DocumentoDAO documentoDAO;

    @Inject
    protected sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaDAO personaDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<Documento, UUID> getDAO() {
        return documentoDAO;
    }

    @Override
    protected Documento crearNuevoRegistro() {
        return new Documento(UUID.randomUUID());
    }

    /**
     * Método para p:autoComplete. Busca Personas por nombres o apellidos.
     */
    public java.util.List<sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona> completePersona(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return personaDAO.findRange(0, 20, query);
    }

    public DocumentoDAO getDocumentoDAO() {
        return documentoDAO;
    }

    public void setDocumentoDAO(DocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
    }

    @Override
    protected void validarNegocio(Documento registro) {
        registro.setValor(ValidadorComun.textoObligatorio(registro.getValor(), "El documento"));
        ValidadorComun.requerido(registro.getIdPersona(), "Seleccione una persona.");
        var tipo = ValidadorComun.requerido(registro.getIdTipoDocumento(), "Seleccione un tipo de documento.");
        ValidadorComun.requerido(tipo.getIdTipoDocumento(), "Seleccione un tipo de documento válido.");
        ValidadorComun.activo(tipo.getActivo(), "El tipo de documento");
        ValidadorComun.formato(registro.getValor(), tipo.getExpresionRegular(), tipo.getIndicaciones());
        if (documentoDAO.existeTipoValor(tipo.getIdTipoDocumento(), registro.getValor(), registro.getIdDocumento())) {
            throw new ValidacionNegocioException("Ya existe un documento de este tipo con el mismo valor.");
        }
    }

}
