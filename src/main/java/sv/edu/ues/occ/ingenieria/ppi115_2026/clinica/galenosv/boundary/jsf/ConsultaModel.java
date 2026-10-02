package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Backing bean de Consulta. Administra embebidos sus procedimientos
 * (ConsultaProcedimiento) y, para el procedimiento activo, sus pasos
 * (ConsultaProcedimientoPaso).
 */
@Named("consultaModel")
@ViewScoped
public class ConsultaModel extends ModelTransaccional<Consulta, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ConsultaDAO consultaDAO;
    @Inject
    protected PersonaRolDAO personaRolDAO;
    @Inject
    protected ConsultaProcedimientoDAO consultaProcedimientoDAO;
    @Inject
    protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;

    private List<ConsultaProcedimiento> procedimientos = Collections.emptyList();
    private ConsultaProcedimiento procedimientoNuevo;

    /** Procedimiento cuyos pasos se están administrando; null si ninguno. */
    private ConsultaProcedimiento procedimientoActivo;
    private List<ConsultaProcedimientoPaso> pasos = Collections.emptyList();
    private ConsultaProcedimientoPaso pasoNuevo;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
        limpiarBorradores();
    }

    @Override
    protected DAOInterface<Consulta, UUID> getDAO() {
        return consultaDAO;
    }

    @Override
    protected Consulta crearNuevoRegistro() {
        Consulta consulta = new Consulta(UUID.randomUUID());
        consulta.setFechaInicio(new Date());
        return consulta;
    }

    /** Método para p:autoComplete. Busca asignaciones Persona/Rol. */
    public List<PersonaRol> completePersonaRol(String query) {
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return personaRolDAO.buscarParaAutocompletar(query, 20);
    }


    @Override
    public void seleccionar(Consulta consulta) {
        super.seleccionar(consulta);
        procedimientos = consultaProcedimientoDAO.findByConsulta(consulta.getIdConsulta());
        cerrarPasos();
        limpiarBorradores();
    }

    @Override
    public void prepararNuevo() {
        super.prepararNuevo();
        procedimientos = Collections.emptyList();
        cerrarPasos();
        limpiarBorradores();
    }

    @Override
    public void cancelar() {
        super.cancelar();
        procedimientos = Collections.emptyList();
        cerrarPasos();
        limpiarBorradores();
    }

    private void cerrarPasos() {
        procedimientoActivo = null;
        pasos = Collections.emptyList();
    }

    private void limpiarBorradores() {
        ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
        cp.setFechaInicio(new Date());
        procedimientoNuevo = cp;
        limpiarPasoNuevo();
    }

    private void limpiarPasoNuevo() {
        ConsultaProcedimientoPaso paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        paso.setFechaInicio(new Date());
        pasoNuevo = paso;
    }

    private void recargarProcedimientos() {
        procedimientos = consultaProcedimientoDAO.findByConsulta(
                getRegistroActual().getIdConsulta());
    }

    private void recargarPasos() {
        pasos = consultaProcedimientoPasoDAO.findByConsultaProcedimiento(
                procedimientoActivo.getIdConsultaProcedimiento());
    }

    private static boolean finAnteriorAInicio(Date inicio, Date fin) {
        return inicio != null && fin != null && fin.before(inicio);
    }


    public void agregarProcedimiento() {
        if (!relacionesHabilitadas("Procedimiento")) {
            return;
        }
        if (procedimientoNuevo.getIdProcedimiento() == null
                || procedimientoNuevo.getFechaInicio() == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Procedimiento",
                    "Seleccione el procedimiento e indique la fecha de inicio.");
            return;
        }
        if (finAnteriorAInicio(procedimientoNuevo.getFechaInicio(), procedimientoNuevo.getFechaFin())) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Procedimiento",
                    "La fecha de fin no puede ser anterior a la de inicio.");
            return;
        }
        procedimientoNuevo.setIdConsulta(getRegistroActual());
        ejecutarRelacion("Procedimiento", () -> {
            consultaProcedimientoDAO.create(procedimientoNuevo);
            recargarProcedimientos();
            ConsultaProcedimiento cp = new ConsultaProcedimiento(UUID.randomUUID());
            cp.setFechaInicio(new Date());
            procedimientoNuevo = cp;
        }, "Procedimiento agregado correctamente.");
    }

    /** No permite quitar un procedimiento que aún tiene pasos registrados. */
    public void quitarProcedimiento(ConsultaProcedimiento cp) {
        if (!relacionesHabilitadas("Procedimiento")) {
            return;
        }
        if (cp == null || !procedimientos.contains(cp)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Procedimiento",
                    "Seleccione un procedimiento válido de la consulta.");
            return;
        }
        if (!consultaProcedimientoPasoDAO
                .findByConsultaProcedimiento(cp.getIdConsultaProcedimiento()).isEmpty()) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Procedimiento",
                    "Este procedimiento tiene pasos registrados. Quite primero sus pasos.");
            return;
        }
        ejecutarRelacion("Procedimiento", () -> {
            consultaProcedimientoDAO.delete(cp);
            if (cp.equals(procedimientoActivo)) {
                cerrarPasos();
            }
            recargarProcedimientos();
        }, "Procedimiento quitado correctamente.");
    }

    /** Elige el procedimiento cuyos pasos se mostrarán en el panel inferior. */
    public void seleccionarProcedimiento(ConsultaProcedimiento cp) {
        if (!relacionesHabilitadas("Pasos")) {
            return;
        }
        if (cp == null || !procedimientos.contains(cp)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Pasos",
                    "Seleccione un procedimiento válido de la consulta.");
            return;
        }
        procedimientoActivo = cp;
        recargarPasos();
        limpiarPasoNuevo();
    }


    /** Todo paso nuevo nace PENDIENTE (EstadoPaso.transicionValida(null, PENDIENTE)). */
    public void agregarPaso() {
        if (!relacionesHabilitadas("Paso") ) {
            return;
        }
        if (procedimientoActivo == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Paso",
                    "Primero seleccione un procedimiento.");
            return;
        }
        if (pasoNuevo.getIdPersonaRol() == null || pasoNuevo.getFechaInicio() == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Paso",
                    "Indique el responsable (persona/rol) y la fecha de inicio.");
            return;
        }
        if (finAnteriorAInicio(pasoNuevo.getFechaInicio(), pasoNuevo.getFechaFin())) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Paso",
                    "La fecha de fin no puede ser anterior a la de inicio.");
            return;
        }
        pasoNuevo.setEstado(EstadoPaso.PENDIENTE.name());
        pasoNuevo.setIdConsultaProcedimiento(procedimientoActivo);
        ejecutarRelacion("Paso", () -> {
            consultaProcedimientoPasoDAO.create(pasoNuevo);
            recargarPasos();
            limpiarPasoNuevo();
        }, "Paso agregado correctamente.");
    }

    /** Falla con mensaje amigable si el paso ya tiene órdenes de examen (FK). */
    public void quitarPaso(ConsultaProcedimientoPaso paso) {
        if (!relacionesHabilitadas("Paso")) {
            return;
        }
        if (paso == null || !pasos.contains(paso)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Paso",
                    "Seleccione un paso válido del procedimiento.");
            return;
        }
        ejecutarRelacion("Paso", () -> {
            consultaProcedimientoPasoDAO.delete(paso);
            recargarPasos();
        }, "Paso quitado correctamente.");
    }


    public List<ConsultaProcedimiento> getProcedimientos() { return procedimientos; }
    public ConsultaProcedimiento getProcedimientoNuevo() { return procedimientoNuevo; }
    public ConsultaProcedimiento getProcedimientoActivo() { return procedimientoActivo; }
    public List<ConsultaProcedimientoPaso> getPasos() { return pasos; }
    public ConsultaProcedimientoPaso getPasoNuevo() { return pasoNuevo; }

    public ConsultaDAO getConsultaDAO() {
        return consultaDAO;
    }

    public void setConsultaDAO(ConsultaDAO consultaDAO) {
        this.consultaDAO = consultaDAO;
    }

    @Override
    protected void validarNegocio(Consulta registro) {
        ValidadorComun.rangoFechas(registro.getFechaInicio(), registro.getFechaFin());
    }
}
