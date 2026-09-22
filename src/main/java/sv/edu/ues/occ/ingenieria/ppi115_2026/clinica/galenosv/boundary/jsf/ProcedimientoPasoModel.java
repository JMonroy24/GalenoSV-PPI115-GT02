package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

/**
 * Backing bean JSF para la gestión de la entidad .
 */
@Named("procedimientoPasoModel")
@ViewScoped
public class ProcedimientoPasoModel extends Model<ProcedimientoPaso, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoPasoDAO procedimientoPasoDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<ProcedimientoPaso, UUID> getDAO() {
        return procedimientoPasoDAO;
    }

    @Override
    protected ProcedimientoPaso crearNuevoRegistro() {
        return new ProcedimientoPaso();
    }

    public ProcedimientoPasoDAO getProcedimientoPasoDAO() {
        return procedimientoPasoDAO;
    }

    public void setProcedimientoPasoDAO(ProcedimientoPasoDAO procedimientoPasoDAO) {
        this.procedimientoPasoDAO = procedimientoPasoDAO;
    }
}