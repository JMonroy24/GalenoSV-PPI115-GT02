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
public class DocumentoModel extends Model<Documento, UUID> implements Serializable {

    @Inject
    protected DocumentoDAO documentoDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<Documento, UUID> getDAO() {
        return documentoDAO;
    }

    @Override
    protected Documento crearNuevoRegistro() {
        return new Documento();
    }

    public DocumentoDAO getDocumentoDAO() {
        return documentoDAO;
    }

    public void setDocumentoDAO(DocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
    }
}
