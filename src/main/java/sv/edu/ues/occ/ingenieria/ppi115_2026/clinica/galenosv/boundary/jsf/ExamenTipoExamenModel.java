package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenTipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

/**
 * Backing bean JSF para la gestión de la entidad .
 */
@Named("examenTipoexamenModel")
@ViewScoped
public class ExamenTipoExamenModel extends Model<ExamenTipoExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ExamenTipoExamenDAO examenTipoexamenDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<ExamenTipoExamen, UUID> getDAO() {
        return examenTipoexamenDAO;
    }

    @Override
    protected ExamenTipoExamen crearNuevoRegistro() {
        return new ExamenTipoExamen();
    }

    public ExamenTipoExamenDAO getExamenTipoExamenDAO() {
        return examenTipoexamenDAO;
    }

    public void setExamenTipoExamenDAO(ExamenTipoExamenDAO examenTipoexamenDAO) {
        this.examenTipoexamenDAO = examenTipoexamenDAO;
    }
}