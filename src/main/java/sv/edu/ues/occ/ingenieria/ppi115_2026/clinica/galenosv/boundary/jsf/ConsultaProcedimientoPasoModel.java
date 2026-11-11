package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Backing bean JSF para la gestión de la entidad ConsultaProcedimientoPaso.
 */
@Named("consultaProcedimientoPasoModel")
@ViewScoped
public class ConsultaProcedimientoPasoModel extends ModelTransaccional<ConsultaProcedimientoPaso, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ConsultaProcedimientoPasoService consultaProcedimientoPasoService;

    @Inject
    protected ConsultaProcedimientoService consultaProcedimientoService;

    @Inject
    protected PersonaRolService personaRolService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ConsultaProcedimientoPaso, UUID> getDAO() {
        return consultaProcedimientoPasoService;
    }

    @Override
    protected ConsultaProcedimientoPaso crearNuevoRegistro() {
        ConsultaProcedimientoPaso cpp = new ConsultaProcedimientoPaso(UUID.randomUUID());
        cpp.setFechaInicio(new Date());
        return cpp;
    }

    /** Método para p:autoComplete. Busca procedimientos de consulta. */
    public List<ConsultaProcedimiento> completeConsultaProcedimiento(String query) {
        return completar(query, texto -> consultaProcedimientoService.buscarParaAutocompletar(texto, 20));
    }

    /** Método para p:autoComplete. Busca asignaciones Persona/Rol. */
    public List<PersonaRol> completePersonaRol(String query) {
        return completar(query, texto -> personaRolService.buscarParaAutocompletar(texto, 20));
    }

    public ConsultaProcedimientoPasoService getConsultaProcedimientoPasoService() {
        return consultaProcedimientoPasoService;
    }

    public void setConsultaProcedimientoPasoService(ConsultaProcedimientoPasoService consultaProcedimientoPasoService) {
        this.consultaProcedimientoPasoService = consultaProcedimientoPasoService;
    }

}
