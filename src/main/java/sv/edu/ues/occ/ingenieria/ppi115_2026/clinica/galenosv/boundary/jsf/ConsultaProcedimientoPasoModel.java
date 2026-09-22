package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

/**
 * Backing bean JSF para la gestión de la entidad ConsultaProcedimientoPaso.
 */
@Named("consultaProcedimientoPasoModel")
@ViewScoped
public class ConsultaProcedimientoPasoModel extends ModelTransaccional<ConsultaProcedimientoPaso, UUID> implements Serializable {

    @Inject
    protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ConsultaProcedimientoPaso, UUID> getDAO() {
        return consultaProcedimientoPasoDAO;
    }

    @Override
    protected ConsultaProcedimientoPaso crearNuevoRegistro() {
        return new ConsultaProcedimientoPaso();
    }

    public ConsultaProcedimientoPasoDAO getConsultaProcedimientoPasoDAO() {
        return consultaProcedimientoPasoDAO;
    }

    public void setConsultaProcedimientoPasoDAO(ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO) {
        this.consultaProcedimientoPasoDAO = consultaProcedimientoPasoDAO;
    }
}
