package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

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
        return personaDAO.findRange(0, 20, query);
    }

    public DocumentoDAO getDocumentoDAO() {
        return documentoDAO;
    }

    public void setDocumentoDAO(DocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
    }
}
