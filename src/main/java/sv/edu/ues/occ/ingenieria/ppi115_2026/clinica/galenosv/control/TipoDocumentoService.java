package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

/**
 * Servicio de negocio para la gestión de tipos de documento.
 */
@ApplicationScoped
public class TipoDocumentoService extends DefaultService<TipoDocumento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoDocumentoDAO tipoDocumentoDAO;

    public TipoDocumentoService() {
    }

    public TipoDocumentoService(TipoDocumentoDAO tipoDocumentoDAO) {
        this.tipoDocumentoDAO = tipoDocumentoDAO;
    }

    @Override
    protected DefaultDAO<TipoDocumento, UUID> getDAO() {
        return tipoDocumentoDAO;
    }

    @Override
    protected void validar(TipoDocumento r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre"));
        ValidadorComun.expresionRegular(r.getExpresionRegular());
        if (tipoDocumentoDAO.existePorCampo("nombre", r.getNombre(), r.getIdTipoDocumento())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public List<TipoDocumento> getActivos() {
        return findAllActivos();
    }

    public List<TipoDocumento> findAllActivos() {
        return tipoDocumentoDAO.findAllActivos();
    }

    public TipoDocumentoDAO getTipoDocumentoDAO() {
        return tipoDocumentoDAO;
    }

    public void setTipoDocumentoDAO(TipoDocumentoDAO tipoDocumentoDAO) {
        this.tipoDocumentoDAO = tipoDocumentoDAO;
    }
}
