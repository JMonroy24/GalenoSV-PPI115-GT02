package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;

/**
 * Backing bean JSF para la gestión de la entidad ExamenResultado.
 */
@Named("examenResultadoModel")
@ViewScoped
public class ExamenResultadoModel extends ModelTransaccional<ExamenResultado, UUID> implements Serializable {

    @Inject
    protected ExamenResultadoDAO examenResultadoDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ExamenResultado, UUID> getDAO() {
        return examenResultadoDAO;
    }

    @Override
    protected ExamenResultado crearNuevoRegistro() {
        return new ExamenResultado();
    }

    public ExamenResultadoDAO getExamenResultadoDAO() {
        return examenResultadoDAO;
    }

    public void setExamenResultadoDAO(ExamenResultadoDAO examenResultadoDAO) {
        this.examenResultadoDAO = examenResultadoDAO;
    }
}
