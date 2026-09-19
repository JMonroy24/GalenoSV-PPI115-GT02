package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

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
public class TipoMedioContactoModel extends Model<TipoMedioContacto, UUID> implements Serializable {

    @Inject
    protected TipoMedioContactoDAO tipoMedioContactoDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<TipoMedioContacto, UUID> getDAO() {
        return tipoMedioContactoDAO;
    }

    @Override
    protected TipoMedioContacto crearNuevoRegistro() {
        return new TipoMedioContacto();
    }

    public TipoMedioContactoDAO getTipoMedioContactoDAO() {
        return tipoMedioContactoDAO;
    }

    public void setTipoMedioContactoDAO(TipoMedioContactoDAO tipoMedioContactoDAO) {
        this.tipoMedioContactoDAO = tipoMedioContactoDAO;
    }
}
