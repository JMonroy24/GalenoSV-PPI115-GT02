package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

/**
 * Backing bean JSF para la gestión de la entidad ExamenTipoExamen.
 */
@Named("examenTipoexamenModel")
@ViewScoped
public class ExamenTipoExamenModel extends ModelTransaccional<ExamenTipoExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ExamenTipoExamenService examenTipoExamenService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ExamenTipoExamen, UUID> getDAO() {
        return examenTipoExamenService;
    }

    @Override
    protected ExamenTipoExamen crearNuevoRegistro() {
        return new ExamenTipoExamen(UUID.randomUUID());
    }

    public ExamenTipoExamenService getExamenTipoExamenService() {
        return examenTipoExamenService;
    }

    public void setExamenTipoExamenService(ExamenTipoExamenService examenTipoExamenService) {
        this.examenTipoExamenService = examenTipoExamenService;
    }
}
