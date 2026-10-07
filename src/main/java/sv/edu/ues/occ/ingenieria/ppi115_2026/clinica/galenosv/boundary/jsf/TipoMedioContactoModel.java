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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoMedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoMedioContacto;

/**
 * Backing bean JSF para la gestión de la entidad TipoMedioContacto.
 */
@Named("tipoMedioContactoModel")
@ViewScoped
public class TipoMedioContactoModel extends ModelTransaccional<TipoMedioContacto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected TipoMedioContactoDAO tipoMedioContactoDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<TipoMedioContacto, UUID> getDAO() {
        return tipoMedioContactoDAO;
    }

    @Override
    protected TipoMedioContacto crearNuevoRegistro() {
        TipoMedioContacto registro = new TipoMedioContacto(UUID.randomUUID());
        registro.setActivo(true);
        return registro;
    }

    public TipoMedioContactoDAO getTipoMedioContactoDAO() {
        return tipoMedioContactoDAO;
    }

    public void setTipoMedioContactoDAO(TipoMedioContactoDAO tipoMedioContactoDAO) {
        this.tipoMedioContactoDAO = tipoMedioContactoDAO;
    }

    @Override
    protected void validarNegocio(TipoMedioContacto registro) {
        registro.setNombre(ValidadorComun.textoObligatorio(registro.getNombre(), "El nombre"));
        ValidadorComun.expresionRegular(registro.getExpresionRegular());
        if (tipoMedioContactoDAO.existePorCampo("nombre", registro.getNombre(), registro.getIdTipoMedioContacto())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public java.util.List<TipoMedioContacto> getActivos() {
        return tipoMedioContactoDAO.findAllActivos();
    }

}
