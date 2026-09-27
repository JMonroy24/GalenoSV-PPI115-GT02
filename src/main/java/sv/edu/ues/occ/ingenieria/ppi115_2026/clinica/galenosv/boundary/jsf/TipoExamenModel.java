package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.TipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

/**
 * Backing bean JSF para la gestión de la entidad TipoExamen.
 */
@Named("tipoExamenModel")
@ViewScoped
public class TipoExamenModel extends ModelTransaccional<TipoExamen, UUID> implements Serializable {

    @Inject
    protected TipoExamenDAO tipoExamenDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<TipoExamen, UUID> getDAO() {
        return tipoExamenDAO;
    }

    @Override
    protected TipoExamen crearNuevoRegistro() {
        return new TipoExamen(UUID.randomUUID());
    }

    public TipoExamenDAO getTipoExamenDAO() {
        return tipoExamenDAO;
    }

    public void setTipoExamenDAO(TipoExamenDAO tipoExamenDAO) {
        this.tipoExamenDAO = tipoExamenDAO;
    }
}
