package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

/**
 * Servicio de negocio para la gestión de tipos de medio de contacto.
 */
@ApplicationScoped
public class TipoMedioContactoService extends DefaultService<TipoMedioContacto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoMedioContactoDAO tipoMedioContactoDAO;

    public TipoMedioContactoService() {
    }

    public TipoMedioContactoService(TipoMedioContactoDAO tipoMedioContactoDAO) {
        this.tipoMedioContactoDAO = tipoMedioContactoDAO;
    }

    @Override
    protected DefaultDAO<TipoMedioContacto, UUID> getDAO() {
        return tipoMedioContactoDAO;
    }

    @Override
    protected void validar(TipoMedioContacto r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre"));
        ValidadorComun.expresionRegular(r.getExpresionRegular());
        if (tipoMedioContactoDAO.existePorCampo("nombre", r.getNombre(), r.getIdTipoMedioContacto())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public List<TipoMedioContacto> getActivos() {
        return findAllActivos();
    }

    public List<TipoMedioContacto> findAllActivos() {
        return tipoMedioContactoDAO.findAllActivos();
    }

    public TipoMedioContactoDAO getTipoMedioContactoDAO() {
        return tipoMedioContactoDAO;
    }

    public void setTipoMedioContactoDAO(TipoMedioContactoDAO tipoMedioContactoDAO) {
        this.tipoMedioContactoDAO = tipoMedioContactoDAO;
    }
}
