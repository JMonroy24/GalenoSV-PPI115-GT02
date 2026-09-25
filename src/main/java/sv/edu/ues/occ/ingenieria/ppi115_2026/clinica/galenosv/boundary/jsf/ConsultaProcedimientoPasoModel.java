package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Backing bean JSF para la gestión de la entidad ConsultaProcedimientoPaso.
 */
@Named("consultaProcedimientoPasoModel")
@ViewScoped
public class ConsultaProcedimientoPasoModel extends ModelTransaccional<ConsultaProcedimientoPaso, UUID> implements Serializable {

    @Inject
    protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

    @Inject
    protected ConsultaProcedimientoDAO consultaProcedimientoDAO;

    @Inject
    protected PersonaRolDAO personaRolDAO;

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
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        cpp.setFechaInicio(new Date());
        return cpp;
    }

    /** Método para p:autoComplete. Busca procedimientos de consulta. */
    public List<ConsultaProcedimiento> completeConsultaProcedimiento(String query) {
        return consultaProcedimientoDAO.buscarParaAutocompletar(query, 20);
    }

    /** Método para p:autoComplete. Busca asignaciones Persona/Rol. */
    public List<PersonaRol> completePersonaRol(String query) {
        return personaRolDAO.buscarParaAutocompletar(query, 20);
    }

    public ConsultaProcedimientoPasoDAO getConsultaProcedimientoPasoDAO() {
        return consultaProcedimientoPasoDAO;
    }

    public void setConsultaProcedimientoPasoDAO(ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO) {
        this.consultaProcedimientoPasoDAO = consultaProcedimientoPasoDAO;
    }
}
