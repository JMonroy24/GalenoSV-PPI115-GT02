package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

/**
 * Backing bean JSF para la gestión de la entidad TipoDocumento.
 */
@Named("tipoDocumentoModel")
@ViewScoped
public class TipoDocumentoModel extends ModelTransaccional<TipoDocumento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoDocumentoService tipoDocumentoService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<TipoDocumento, UUID> getDAO() {
        return tipoDocumentoService;
    }

    @Override
    protected TipoDocumento crearNuevoRegistro() {
        TipoDocumento registro = new TipoDocumento(UUID.randomUUID());
        registro.setActivo(true);
        return registro;
    }

    public TipoDocumentoService getTipoDocumentoService() {
        return tipoDocumentoService;
    }

    public void setTipoDocumentoService(TipoDocumentoService tipoDocumentoService) {
        this.tipoDocumentoService = tipoDocumentoService;
    }

    public java.util.List<TipoDocumento> getActivos() {
        return tipoDocumentoService.getActivos();
    }
}
