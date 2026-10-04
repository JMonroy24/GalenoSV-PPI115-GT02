package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.AsignacionService;
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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DocumentoDAO;
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
    @Inject protected SesionBean sesionBean;
    @Inject protected ProcedimientoPasoDAO procedimientoPasoDAO;
    @Inject protected AsignacionService asignacionService;
    private Date fechaDesde;
    private Date fechaHasta;
    private GenericLazyDataModel<Consulta> consultasFiltradas;
    @Inject
    protected PersonaRolDAO personaRolDAO;
    @Inject
    protected ConsultaProcedimientoDAO consultaProcedimientoDAO;
    @Inject
    protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;
    @Inject
    protected DocumentoDAO documentoDAO;

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
        if (idClinicaActual() == null) return List.of();
        return personaRolDAO.buscarPacientes(query, idClinicaActual(), 20);
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
        final PersonaRol responsable;
        try {
            validarPaciente(getRegistroActual());
            ValidadorComun.activo(procedimientoNuevo.getIdProcedimiento().getActivo(), "El procedimiento");
            ValidadorComun.dentroDelPeriodo(procedimientoNuevo.getFechaInicio(), procedimientoNuevo.getFechaFin(),
                    getRegistroActual().getFechaInicio(), getRegistroActual().getFechaFin());
            var inicio = ValidadorComun.requerido(procedimientoPasoDAO.findPasoInicial(
                    procedimientoNuevo.getIdProcedimiento().getIdProcedimiento()), "El procedimiento no tiene pasos definidos.");
            var rol = ValidadorComun.requerido(inicio.getIdRol(), "El paso inicial debe tener un rol responsable.");
            ValidadorComun.activo(rol.getActivo(), "El rol del paso inicial");
            responsable = ValidadorComun.requerido(personaRolDAO.findResponsable(idClinicaActual(), rol.getIdRol()),
                    "No hay una persona con el rol requerido en la clínica actual.");
        } catch (ValidacionNegocioException e) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Procedimiento", e.getMessage());
            return;
        }
        procedimientoNuevo.setIdConsulta(getRegistroActual());
        ejecutarRelacion("Procedimiento", () -> {
            ConsultaProcedimientoPaso inicio = new ConsultaProcedimientoPaso(UUID.randomUUID());
            inicio.setEstado(EstadoPaso.PENDIENTE.name());
            inicio.setFechaInicio(procedimientoNuevo.getFechaInicio());
            inicio.setIdPersonaRol(responsable);
            inicio.setIdConsultaProcedimiento(procedimientoNuevo);
            asignacionService.crearProcedimientoConPaso(procedimientoNuevo, inicio);
            procedimientoActivo = procedimientoNuevo;
            recargarProcedimientos();
            recargarPasos();
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
        validarPaciente(registro);
        ValidadorComun.rangoFechas(registro.getFechaInicio(), registro.getFechaFin());
    }

    private UUID idClinicaActual() {
        var clinica = sesionBean == null ? null : sesionBean.getClinicaActual();
        return clinica == null || !Boolean.TRUE.equals(clinica.getActivo()) ? null : clinica.getIdClinica();
    }

    private void validarPaciente(Consulta registro) {
        UUID clinica = ValidadorComun.requerido(idClinicaActual(),
                "Seleccione una clínica de trabajo antes de crear consultas.");
        var personaRol = registro.getIdPersonaRol();
        var rol = personaRol == null ? null : personaRol.getIdRol();
        if (personaRol == null || personaRol.getIdClinica() == null
                || !clinica.equals(personaRol.getIdClinica().getIdClinica())
                || rol == null || !Boolean.TRUE.equals(rol.getActivo()) || rol.getNombre() == null
                || !rol.getNombre().toLowerCase(java.util.Locale.ROOT).contains(PersonaRolDAO.ROL_PACIENTE)) {
            throw new ValidacionNegocioException("La persona debe ser paciente de la clínica de trabajo.");
        }
    }

    @Override
    protected void inicializarLazyModel() {
        consultasFiltradas = new GenericLazyDataModel<>(consultaDAO) {
            private static final long serialVersionUID = 1L;
            @Override
            public int count(java.util.Map<String, org.primefaces.model.FilterMeta> filtros) {
                if (!rangoFiltroValido()) return 0;
                return Math.toIntExact(consultaDAO.countFiltrado(idClinicaActual(), inicioDia(fechaDesde),
                        finDia(fechaHasta), getFiltroGlobal()));
            }
            @Override
            public List<Consulta> load(int first, int max,
                    java.util.Map<String, org.primefaces.model.SortMeta> orden,
                    java.util.Map<String, org.primefaces.model.FilterMeta> filtros) {
                if (!rangoFiltroValido()) return List.of();
                return consultaDAO.findRangeFiltrado(first, max, idClinicaActual(), inicioDia(fechaDesde),
                        finDia(fechaHasta), getFiltroGlobal());
            }
        };
    }

    @Override
    public GenericLazyDataModel<Consulta> getLazyModel() { return consultasFiltradas; }
    public Date getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(Date fechaDesde) { this.fechaDesde = fechaDesde; }
    public Date getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(Date fechaHasta) { this.fechaHasta = fechaHasta; }

    private Date inicioDia(Date fecha) {
        if (fecha == null) return null;
        var zona = java.time.ZoneId.of("America/El_Salvador");
        return Date.from(fecha.toInstant().atZone(zona).toLocalDate().atStartOfDay(zona).toInstant());
    }

    private Date finDia(Date fecha) {
        if (fecha == null) return null;
        return new Date(inicioDia(fecha).getTime() + java.time.Duration.ofDays(1).toMillis() - 1);
    }

    private boolean rangoFiltroValido() {
        return fechaDesde == null || fechaHasta == null || !finDia(fechaHasta).before(inicioDia(fechaDesde));
    }

    public void filtrar() {
        setSeleccion(null);
        if (!rangoFiltroValido()) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Fechas", "La fecha hasta no puede ser anterior a desde.");
            marcarValidacionFallida();
        }
    }

    public void limpiarFiltro() { fechaDesde = null; fechaHasta = null; filtrar(); }

}
