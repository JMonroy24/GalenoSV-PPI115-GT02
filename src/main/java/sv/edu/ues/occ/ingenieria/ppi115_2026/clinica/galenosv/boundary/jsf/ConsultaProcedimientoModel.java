package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

/**
 * Backing bean JSF para la gestión de la entidad ConsultaProcedimiento.
 */
@Named("consultaProcedimientoModel")
@ViewScoped
public class ConsultaProcedimientoModel extends ModelTransaccional<ConsultaProcedimiento, UUID> implements Serializable {

    @Inject
    protected ConsultaProcedimientoDAO consultaProcedimientoDAO;

    @Inject
    protected ConsultaDAO consultaDAO;

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
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        cp.setFechaInicio(new Date());
        return cp;
    }

    /** Método para p:autoComplete. Busca consultas por persona o referencia. */
    public List<Consulta> completeConsulta(String query) {
        return consultaDAO.buscarParaAutocompletar(query, 20);
    }

    public ConsultaProcedimientoDAO getConsultaProcedimientoDAO() {
        return consultaProcedimientoDAO;
    }

    public void setConsultaProcedimientoDAO(ConsultaProcedimientoDAO consultaProcedimientoDAO) {
        this.consultaProcedimientoDAO = consultaProcedimientoDAO;
    }
}
