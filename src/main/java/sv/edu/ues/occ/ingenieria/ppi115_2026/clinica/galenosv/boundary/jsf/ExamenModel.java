package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Examen;

/**
 * Backing bean JSF para la gestión de la entidad {@link Examen}.
 */
@Named("examenModel")
@ViewScoped
public class ExamenModel extends Model<Examen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ExamenDAO examenDAO;

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<Examen, UUID> getDAO() {
        return examenDAO;
    }

    @Override
    protected Examen crearNuevoRegistro() {
        return new Examen();
    }

    public ExamenDAO getExamenDAO() {
        return examenDAO;
    }

    public void setExamenDAO(ExamenDAO examenDAO) {
        this.examenDAO = examenDAO;
    }
}