package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

/**
 * Servicio de negocio para la gestión de roles.
 */
@ApplicationScoped
public class RolService extends DefaultService<Rol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected RolDAO rolDAO;

    public RolService() {
    }

    public RolService(RolDAO rolDAO) {
        this.rolDAO = rolDAO;
    }

    @Override
    protected DefaultDAO<Rol, UUID> getDAO() {
        return rolDAO;
    }

    @Override
    protected void validar(Rol r, boolean nuevo) {
        r.setNombre(ValidadorComun.textoObligatorio(r.getNombre(), "El nombre"));
        if (rolDAO.existePorCampo("nombre", r.getNombre(), r.getIdRol())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

    public List<Rol> getRolesActivos() {
        return findAllActivos();
    }

    public List<Rol> findAllActivos() {
        return rolDAO.findAllActivos();
    }

    public RolDAO getRolDAO() {
        return rolDAO;
    }

    public void setRolDAO(RolDAO rolDAO) {
        this.rolDAO = rolDAO;
    }
}
