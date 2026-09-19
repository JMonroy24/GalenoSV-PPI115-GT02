package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

/**
 * Backing bean JSF para la gestión de la entidad Rol.
 */
@Named("rolModel")
@ViewScoped
public class RolModel extends Model<Rol, UUID> implements Serializable {

    @Inject
    protected RolDAO rolDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<Rol, UUID> getDAO() {
        return rolDAO;
    }

    @Override
    protected Rol crearNuevoRegistro() {
        return new Rol();
    }

    public RolDAO getRolDAO() {
        return rolDAO;
    }

    public void setRolDAO(RolDAO rolDAO) {
        this.rolDAO = rolDAO;
    }
}
