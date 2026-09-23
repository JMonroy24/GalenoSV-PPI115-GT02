package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

/**
 * Backing bean JSF para la gestión de la entidad OrdenExamen.
 */
@Named("ordenExamenModel")
@ViewScoped
public class OrdenExamenModel extends ModelTransaccional<OrdenExamen, UUID> implements Serializable {

    @Inject
    protected OrdenExamenDAO ordenExamenDAO;

    @Inject
    protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

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
        OrdenExamen o = new OrdenExamen(UUID.randomUUID());
        o.setFechaCreacion(new Date());
        return o;
    }

    /** Método para p:autoComplete. Busca pasos de procedimiento de consulta. */
    public List<ConsultaProcedimientoPaso> completeConsultaProcedimientoPaso(String query) {
        return consultaProcedimientoPasoDAO.buscarParaAutocompletar(query, 20);
    }

    public OrdenExamenDAO getOrdenExamenDAO() {
        return ordenExamenDAO;
    }

    public void setOrdenExamenDAO(OrdenExamenDAO ordenExamenDAO) {
        this.ordenExamenDAO = ordenExamenDAO;
    }
}
