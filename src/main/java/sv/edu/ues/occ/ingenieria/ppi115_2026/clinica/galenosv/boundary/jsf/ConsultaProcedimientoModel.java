package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

/**
 * Backing bean JSF para la gestión de la entidad ConsultaProcedimiento.
 */
@Named("consultaProcedimientoModel")
@ViewScoped
public class ConsultaProcedimientoModel extends ModelTransaccional<ConsultaProcedimiento, UUID> implements Serializable {

    @Inject
    protected ConsultaProcedimientoDAO consultaProcedimientoDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ConsultaProcedimiento, UUID> getDAO() {
        return consultaProcedimientoDAO;
    }

    @Override
    protected ConsultaProcedimiento crearNuevoRegistro() {
        return new ConsultaProcedimiento();
    }

    public ConsultaProcedimientoDAO getConsultaProcedimientoDAO() {
        return consultaProcedimientoDAO;
    }

    public void setConsultaProcedimientoDAO(ConsultaProcedimientoDAO consultaProcedimientoDAO) {
        this.consultaProcedimientoDAO = consultaProcedimientoDAO;
    }
}
