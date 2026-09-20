package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

/**
 * Backing bean JSF para la gestión de la entidad {@link Procedimiento}.
 */
@Named("procedimientoModel")
@ViewScoped
public class ProcedimientoModel extends Model<Procedimiento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoDAO procedimientoDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<Procedimiento, UUID> getDAO() {
        return procedimientoDAO;
    }

    @Override
    protected Procedimiento crearNuevoRegistro() {
        return new Procedimiento();
    }

    public ProcedimientoDAO getProcedimientoDAO() {
        return procedimientoDAO;
    }

    public void setProcedimientoDAO(ProcedimientoDAO procedimientoDAO) {
        this.procedimientoDAO = procedimientoDAO;
    }
}