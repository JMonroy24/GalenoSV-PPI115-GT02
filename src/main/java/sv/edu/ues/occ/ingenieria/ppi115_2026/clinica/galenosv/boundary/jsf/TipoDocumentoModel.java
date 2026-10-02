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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

/**
 * Backing bean JSF para la gestión de la entidad TipoDocumento.
 */
@Named("tipoDocumentoModel")
@ViewScoped
public class TipoDocumentoModel extends ModelTransaccional<TipoDocumento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoDocumentoDAO tipoDocumentoDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<TipoDocumento, UUID> getDAO() {
        return tipoDocumentoDAO;
    }

    @Override
    protected TipoDocumento crearNuevoRegistro() {
        return new TipoDocumento(UUID.randomUUID());
    }

    public TipoDocumentoDAO getTipoDocumentoDAO() {
        return tipoDocumentoDAO;
    }

    public void setTipoDocumentoDAO(TipoDocumentoDAO tipoDocumentoDAO) {
        this.tipoDocumentoDAO = tipoDocumentoDAO;
    }

    @Override
    protected void validarNegocio(TipoDocumento registro) {
        registro.setNombre(ValidadorComun.textoObligatorio(registro.getNombre(), "El nombre"));
        ValidadorComun.expresionRegular(registro.getExpresionRegular());
        if (tipoDocumentoDAO.existePorCampo("nombre", registro.getNombre(), registro.getIdTipoDocumento())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public java.util.List<TipoDocumento> getActivos() {
        return tipoDocumentoDAO.findAllActivos();
    }

}
