package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

/**
 * Backing bean JSF para la gestión de la entidad Rol.
 */
@Named("rolModel")
@ViewScoped
public class RolModel extends ModelTransaccional<Rol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected RolService rolService;

    @PostConstruct
    public void init() {
        inicializarLazyModel(true);
    }

    @Override
    protected DAOInterface<Rol, UUID> getDAO() {
        return rolService;
    }

    @Override
    protected Rol crearNuevoRegistro() {
        Rol registro = new Rol(UUID.randomUUID());
        registro.setActivo(true);
        return registro;
    }

    /**
     * Fuente de opciones para selectores de FK en otras vistas.
     * Retorna solo los roles activos, sin cargar el modelo completo.
     *
     * @return lista de Rol ordenada por nombre
     */
    public java.util.List<Rol> getRolesActivos() {
        return rolService.getRolesActivos();
    }

    public RolService getRolService() {
        return rolService;
    }

    public void setRolService(RolService rolService) {
        this.rolService = rolService;
    }

    public List<Rol> completeRol(String query) {
        return completar(query, 1, texto -> rolService.buscarParaAutocompletar(texto, 20));
    }
}
