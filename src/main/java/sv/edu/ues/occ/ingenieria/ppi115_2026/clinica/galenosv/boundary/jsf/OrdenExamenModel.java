package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

/**
 * Backing bean JSF para la gestión de la entidad OrdenExamen.
 */
@Named("ordenExamenModel")
@ViewScoped
public class OrdenExamenModel extends ModelTransaccional<OrdenExamen, UUID> implements Serializable {

    @Inject
    protected OrdenExamenDAO ordenExamenDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<OrdenExamen, UUID> getDAO() {
        return ordenExamenDAO;
    }

    @Override
    protected OrdenExamen crearNuevoRegistro() {
        return new OrdenExamen();
    }

    public OrdenExamenDAO getOrdenExamenDAO() {
        return ordenExamenDAO;
    }

    public void setOrdenExamenDAO(OrdenExamenDAO ordenExamenDAO) {
        this.ordenExamenDAO = ordenExamenDAO;
    }
}
