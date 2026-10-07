package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.*;

/** Editor de pasos integrado en el procedimiento; las asociaciones se preparan en memoria. */
@Named("procedimientoPasoModel")
@ViewScoped
public class ProcedimientoPasoModel extends Model<ProcedimientoPaso, UUID> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProcedimientoPasoModel.class.getName());
    @Inject protected ProcedimientoPasoDAO procedimientoPasoDAO;
    @Inject protected ProcedimientoPasoExamenDAO procedimientoPasoExamenDAO;
    @Inject protected ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO;
    @Inject protected AsignacionService asignacionService;
    @Inject protected RolDAO rolDAO;
    @Inject protected ExamenDAO examenDAO;
    private Procedimiento procedimiento;
    private ProcedimientoPaso dependeDe;
    private List<ProcedimientoPasoExamen> examenesAsignados = new ArrayList<>();
    private ProcedimientoPasoExamen examenAsignadoSeleccionado;
    private Examen examenSeleccionado;
    private String observacionesExamen;
    private UUID rolOriginal;
    private UUID padreOriginal;

    @PostConstruct public void init() { cargarDatos(); }
    @Override protected DAOInterface<ProcedimientoPaso, UUID> getDAO() { return procedimientoPasoDAO; }
    @Override protected ProcedimientoPaso crearNuevoRegistro() {
        ProcedimientoPaso paso = new ProcedimientoPaso(UUID.randomUUID());
        paso.setIdProcedimiento(procedimiento);
        paso.setIndicaFin(false);
        return paso;
    }
    public void abrirProcedimiento(Procedimiento actual) {
        procedimiento = actual;
        prepararNuevo();
    }
    @Override public void prepararNuevo() {
        super.prepararNuevo();
        dependeDe = null;
        rolOriginal = null;
        padreOriginal = null;
        examenesAsignados = new ArrayList<>();
        limpiarExamen();
    }
    @Override public void cancelar() { prepararNuevo(); }
    @Override public void seleccionar(ProcedimientoPaso paso) {
        // Copia editable para que cancelar no altere los datos visibles del árbol.
        ProcedimientoPaso copia = new ProcedimientoPaso(paso.getIdProcedimientoPaso());
        copia.setIdProcedimiento(procedimiento);
        copia.setNombre(paso.getNombre());
        copia.setIdRol(paso.getIdRol());
        copia.setIndicaFin(paso.getIndicaFin());
        super.seleccionar(copia);
        dependeDe = null;
        for (ProcedimientoPasoSecuencia s : procedimientoPasoSecuenciaDAO.findByProcedimiento(
                procedimiento.getIdProcedimiento())) {
            if ("SIGUIENTE".equals(s.getTipoSecuencia())
                    && paso.getIdProcedimientoPaso().equals(s.getIdProcedimientoPasoReferencia()))
                dependeDe = s.getIdProcedimientoPaso();
        }
        rolOriginal = paso.getIdRol() == null ? null : paso.getIdRol().getIdRol();
        padreOriginal = dependeDe == null ? null : dependeDe.getIdProcedimientoPaso();
        examenesAsignados = new ArrayList<>(procedimientoPasoExamenDAO.findByPaso(paso.getIdProcedimientoPaso()));
        limpiarExamen();
    }
    private void limpiarExamen() {
        examenSeleccionado = null;
        observacionesExamen = null;
        examenAsignadoSeleccionado = null;
    }
    public void agregarExamen() {
        if (getRegistroActual() == null || examenSeleccionado == null
                || !Boolean.TRUE.equals(examenSeleccionado.getActivo())) {
            error("Seleccione un examen activo."); return;
        }
        if (examenesAsignados.stream().anyMatch(a -> examenSeleccionado.equals(a.getIdExamen()))) {
            error("Este examen ya está asignado al paso."); return;
        }
        ProcedimientoPasoExamen asignacion = new ProcedimientoPasoExamen(UUID.randomUUID());
        asignacion.setIdProcedimientoPaso(getRegistroActual());
        asignacion.setIdExamen(examenSeleccionado);
        asignacion.setFechaCreacion(new Date());
        asignacion.setActivo(true);
        asignacion.setObservaciones(observacionesExamen);
        examenesAsignados.add(asignacion);
        limpiarExamen();
    }
    public void quitarExamenSeleccionado() {
        if (examenAsignadoSeleccionado == null || !examenesAsignados.remove(examenAsignadoSeleccionado))
            error("Seleccione un examen asignado a este paso.");
        examenAsignadoSeleccionado = null;
    }
    public List<ProcedimientoPaso> getPasosDependenciaDisponibles() {
        if (procedimiento == null) return List.of();
        return procedimientoPasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento()).stream()
                .filter(p -> !p.equals(getRegistroActual()))
                .sorted(Comparator.comparing(ProcedimientoPaso::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }
    public List<Rol> getRolesActivos() {
        return rolDAO.findAll().stream().filter(r -> Boolean.TRUE.equals(r.getActivo())).toList();
    }
    public List<Examen> getExamenesActivos() {
        return examenDAO.findAll().stream().filter(e -> Boolean.TRUE.equals(e.getActivo())).toList();
    }
    protected boolean validarNegocio() {
        ProcedimientoPaso paso = getRegistroActual();
        if (paso == null || procedimiento == null || !procedimiento.equals(paso.getIdProcedimiento()))
            return error("Seleccione el procedimiento actual.");
        if (paso.getNombre() == null || paso.getNombre().isBlank()) return error("Ingrese el nombre del paso.");
        Rol rol = paso.getIdRol() == null ? null : rolDAO.findById(paso.getIdRol().getIdRol());
        if (rol == null || !Boolean.TRUE.equals(rol.getActivo())) return error("Seleccione un rol activo.");
        List<ProcedimientoPaso> otros = procedimientoPasoDAO.findByProcedimiento(procedimiento.getIdProcedimiento())
                .stream().filter(p -> !p.equals(paso)).toList();
        if (isEstadoModificar() && (!Objects.equals(rolOriginal, rol.getIdRol())
                || !Objects.equals(padreOriginal, dependeDe == null ? null : dependeDe.getIdProcedimientoPaso())))
            return error("El rol y la dependencia solo se pueden elegir al crear el paso.");
        if (otros.isEmpty() && dependeDe != null) return error("El primer paso no debe tener dependencia.");
        if (isEstadoCrear() && !otros.isEmpty() && dependeDe == null)
            return error("Seleccione el paso del que depende.");
        if (dependeDe != null) {
            ProcedimientoPaso padre = otros.stream().filter(p -> p.equals(dependeDe)).findFirst().orElse(null);
            if (padre == null) return error("La dependencia debe ser otro paso del mismo procedimiento.");
            if (Boolean.TRUE.equals(padre.getIndicaFin())) return error("Un paso de fin no puede tener hijos.");
        }
        List<ProcedimientoPasoSecuencia> secuencias = procedimientoPasoSecuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento());
        if (Boolean.TRUE.equals(paso.getIndicaFin()) && secuencias.stream().anyMatch(s ->
                "SIGUIENTE".equals(s.getTipoSecuencia()) && paso.equals(s.getIdProcedimientoPaso())))
            return error("Un paso con hijos no puede indicar finalización.");
        // Comprueba si el padre ya es descendiente del paso, incluso ante datos heredados corruptos.
        Set<UUID> visitados = new HashSet<>();
        Deque<UUID> pendientes = new ArrayDeque<>();
        pendientes.add(paso.getIdProcedimientoPaso());
        while (!pendientes.isEmpty()) {
            UUID id = pendientes.remove();
            if (!visitados.add(id)) continue;
            if (dependeDe != null && id.equals(dependeDe.getIdProcedimientoPaso()))
                return error("La dependencia produciría un ciclo.");
            secuencias.stream().filter(s -> "SIGUIENTE".equals(s.getTipoSecuencia())
                    && s.getIdProcedimientoPaso() != null
                    && id.equals(s.getIdProcedimientoPaso().getIdProcedimientoPaso()))
                    .map(ProcedimientoPasoSecuencia::getIdProcedimientoPasoReferencia)
                    .filter(Objects::nonNull).forEach(pendientes::add);
        }
        Set<UUID> examenes = new HashSet<>();
        for (ProcedimientoPasoExamen a : examenesAsignados) {
            Examen examen = a.getIdExamen() == null ? null : examenDAO.findById(a.getIdExamen().getIdExamen());
            if (examen == null || !Boolean.TRUE.equals(examen.getActivo())) return error("Todos los exámenes deben estar activos.");
            if (!examenes.add(examen.getIdExamen())) return error("No se permiten exámenes duplicados.");
        }
        return true;
    }
    private boolean error(String detalle) {
        agregarMensaje(FacesMessage.SEVERITY_ERROR, "Paso de procedimiento", detalle);
        return false;
    }
    /** Devuelve éxito para que el procedimiento refresque el árbol solo tras guardar. */
    public boolean guardarPaso() {
        try {
            if (!validarNegocio()) return false;
            asignacionService.guardarPaso(getRegistroActual(), isEstadoCrear(), dependeDe, examenesAsignados);
            prepararNuevo();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Paso", "Paso y exámenes guardados correctamente.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar paso", e);
            error(clasificarError(e)); return false;
        }
    }
    @Override public void guardar() { guardarPaso(); }
    public boolean eliminarPaso(ProcedimientoPaso paso) {
        try {
            if (procedimiento == null || !procedimiento.equals(paso.getIdProcedimiento()))
                return error("El paso debe pertenecer al procedimiento actual.");
            if (procedimientoPasoSecuenciaDAO.findByProcedimiento(procedimiento.getIdProcedimiento()).stream()
                    .anyMatch(s -> "SIGUIENTE".equals(s.getTipoSecuencia()) && paso.equals(s.getIdProcedimientoPaso())))
                return error("No se puede eliminar un paso con hijos. Elimine primero sus hijos.");
            asignacionService.eliminarPaso(paso);
            prepararNuevo();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Paso", "Paso eliminado correctamente.");
            return true;
        } catch (Exception e) { error(clasificarError(e)); return false; }
    }
    @Override public void eliminar(ProcedimientoPaso paso) { eliminarPaso(paso); }
    public ProcedimientoPaso getDependeDe() { return dependeDe; }
    public void setDependeDe(ProcedimientoPaso paso) { dependeDe = paso; }
    public List<ProcedimientoPasoExamen> getExamenesAsignados() { return examenesAsignados; }
    public ProcedimientoPasoExamen getExamenAsignadoSeleccionado() { return examenAsignadoSeleccionado; }
    public void setExamenAsignadoSeleccionado(ProcedimientoPasoExamen examen) { examenAsignadoSeleccionado = examen; }
    public Examen getExamenSeleccionado() { return examenSeleccionado; }
    public void setExamenSeleccionado(Examen examen) { examenSeleccionado = examen; }
    public String getObservacionesExamen() { return observacionesExamen; }
    public void setObservacionesExamen(String observaciones) { observacionesExamen = observaciones; }
    public ProcedimientoPasoDAO getProcedimientoPasoDAO() { return procedimientoPasoDAO; }
    public void setProcedimientoPasoDAO(ProcedimientoPasoDAO dao) { procedimientoPasoDAO = dao; }
}
