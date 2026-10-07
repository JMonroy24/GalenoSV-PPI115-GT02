package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.faces.application.FacesMessage;
import org.primefaces.model.TreeNode;
import org.primefaces.model.DefaultTreeNode;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

/**
 * Administra el catálogo de procedimientos clínicos.
 * La gestión de los pasos se realiza desde ProcedimientoPasoModel.
 */
@Named("procedimientoModel")
@ViewScoped
public class ProcedimientoModel extends Model<Procedimiento, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoDAO procedimientoDAO;

    @Inject protected ProcedimientoPasoDAO pasoDAO;
    @Inject protected ProcedimientoPasoSecuenciaDAO secuenciaDAO;
    @Inject protected ProcedimientoPasoModel pasoModel;
    private TreeNode<ProcedimientoPaso> arbolPasos;
    private List<ProcedimientoPaso> pasos = List.of();
    private final Map<UUID, ProcedimientoPaso> padres = new HashMap<>();
    private int pestanaActiva;
    private Procedimiento procedimientoSeleccionado;
    private TreeNode<ProcedimientoPaso> nodoPasoSeleccionado;

    public Procedimiento getProcedimientoSeleccionado() { return procedimientoSeleccionado; }
    public void setProcedimientoSeleccionado(Procedimiento seleccionado) { procedimientoSeleccionado = seleccionado; }
    public TreeNode<ProcedimientoPaso> getNodoPasoSeleccionado() { return nodoPasoSeleccionado; }
    public void setNodoPasoSeleccionado(TreeNode<ProcedimientoPaso> seleccionado) { nodoPasoSeleccionado = seleccionado; }
    public void editarSeleccionado() {
        if (procedimientoSeleccionado != null) seleccionar(procedimientoSeleccionado);
    }
    public void eliminarSeleccionado() {
        if (procedimientoSeleccionado != null) {
            eliminar(procedimientoSeleccionado);
            procedimientoSeleccionado = null;
        }
    }
    public void nuevoPaso() {
        nodoPasoSeleccionado = null;
        pasoModel.prepararNuevo();
    }
    public void editarPasoSeleccionado() {
        if (nodoPasoSeleccionado != null) pasoModel.seleccionar(nodoPasoSeleccionado.getData());
    }
    public void eliminarPasoSeleccionado() {
        if (nodoPasoSeleccionado != null) eliminarPaso(nodoPasoSeleccionado.getData());
    }

    @Override public void prepararNuevo() {
        super.prepararNuevo();
        procedimientoSeleccionado = null;
        nodoPasoSeleccionado = null;
        pestanaActiva = 0;
        arbolPasos = null;
    }
    @Override public void seleccionar(Procedimiento procedimiento) {
        super.seleccionar(procedimiento);
        pestanaActiva = 0;
        pasoModel.abrirProcedimiento(procedimiento);
        refrescarPasos();
        advertirSinFin();
    }
    @Override public void cancelar() {
        super.cancelar();
        arbolPasos = null;
        pasos = List.of();
        padres.clear();
    }
    @Override public void guardar() {
        try {
            Procedimiento actual = getRegistroActual();
            if (isEstadoCrear()) procedimientoDAO.create(actual);
            else if (isEstadoModificar()) procedimientoDAO.update(actual);
            else return;
            cargarDatos();
            super.seleccionar(actual);
            pasoModel.abrirProcedimiento(actual);
            refrescarPasos();
            advertirSinFin();
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Procedimiento", "Procedimiento guardado. Puede administrar sus pasos.");
        } catch (Exception e) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Procedimiento", clasificarError(e));
        }
    }
    @Override public void eliminar(Procedimiento procedimiento) {
        Estado anterior = getEstado();
        super.eliminar(procedimiento);
        if (isEstadoEliminar()) setEstado(anterior);
    }
    public void guardarPaso() {
        if (pasoModel.guardarPaso()) {
            refrescarPasos();
            advertirSinFin();
            pestanaActiva = 1;
        }
    }
    public void eliminarPaso(ProcedimientoPaso paso) {
        if (pasoModel.eliminarPaso(paso)) {
            refrescarPasos();
            advertirSinFin();
            pestanaActiva = 1;
        }
    }
    public void advertirSinFin() {
        if (pasos.stream().noneMatch(p -> Boolean.TRUE.equals(p.getIndicaFin())))
            agregarMensaje(FacesMessage.SEVERITY_WARN, "Procedimiento sin finalización",
                    "Este procedimiento aún no tiene un paso de fin.");
    }
    public void refrescarPasos() { nodoPasoSeleccionado = null; arbolPasos = null; getArbolPasos(); }
    /** Construye un bosque expandido: cada paso se muestra una vez, incluso con ciclos u huérfanos. */
    public TreeNode<ProcedimientoPaso> getArbolPasos() {
        if (arbolPasos != null) return arbolPasos;
        arbolPasos = new DefaultTreeNode<>();
        padres.clear();
        if (getRegistroActual() == null || !isEstadoModificar()) return arbolPasos;
        UUID id = getRegistroActual().getIdProcedimiento();
        pasos = new ArrayList<>(pasoDAO.findByProcedimiento(id));
        pasos.sort(Comparator.comparing(ProcedimientoPaso::getNombre,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).thenComparing(ProcedimientoPaso::getIdProcedimientoPaso));
        Map<UUID, ProcedimientoPaso> porId = pasos.stream().collect(Collectors.toMap(
                ProcedimientoPaso::getIdProcedimientoPaso, p -> p));
        Map<UUID, Set<UUID>> hijos = new HashMap<>();
        Set<UUID> referencias = new HashSet<>();
        for (ProcedimientoPasoSecuencia s : secuenciaDAO.findByProcedimiento(id)) {
            if (!"SIGUIENTE".equals(s.getTipoSecuencia()) || s.getIdProcedimientoPaso() == null) continue;
            UUID padre = s.getIdProcedimientoPaso().getIdProcedimientoPaso();
            UUID hijo = s.getIdProcedimientoPasoReferencia();
            if (!porId.containsKey(padre) || !porId.containsKey(hijo)) continue;
            hijos.computeIfAbsent(padre, k -> new HashSet<>()).add(hijo);
            referencias.add(hijo);
        }
        Set<UUID> visitados = new HashSet<>();
        for (ProcedimientoPaso paso : pasos)
            if (!referencias.contains(paso.getIdProcedimientoPaso())) agregarNodo(paso, arbolPasos, hijos, visitados);
        for (ProcedimientoPaso paso : pasos)
            if (!visitados.contains(paso.getIdProcedimientoPaso())) agregarNodo(paso, arbolPasos, hijos, visitados);
        return arbolPasos;
    }
    private void agregarNodo(ProcedimientoPaso paso, TreeNode<ProcedimientoPaso> padre,
                             Map<UUID, Set<UUID>> hijos, Set<UUID> visitados) {
        if (!visitados.add(paso.getIdProcedimientoPaso())) return;
        TreeNode<ProcedimientoPaso> nodo = new DefaultTreeNode<>(paso, padre);
        nodo.setExpanded(true);
        if (padre.getData() != null) padres.put(paso.getIdProcedimientoPaso(), padre.getData());
        Set<UUID> idsHijos = hijos.getOrDefault(paso.getIdProcedimientoPaso(), Set.of());
        for (ProcedimientoPaso hijo : pasos)
            if (idsHijos.contains(hijo.getIdProcedimientoPaso())) agregarNodo(hijo, nodo, hijos, visitados);
    }
    public String procedeDe(ProcedimientoPaso paso) {
        ProcedimientoPaso padre = padres.get(paso.getIdProcedimientoPaso());
        return padre == null ? "— (paso inicial)" : padre.getNombre();
    }
    public boolean esInicial(ProcedimientoPaso paso) { return !padres.containsKey(paso.getIdProcedimientoPaso()); }
    public String examenesDe(ProcedimientoPaso paso) {
        if (paso.getProcedimientoPasoExamenList() == null) return "";
        return paso.getProcedimientoPasoExamenList().stream().filter(a -> a.getIdExamen() != null)
                .map(a -> a.getIdExamen().getNombre()).collect(Collectors.joining(", "));
    }
    public int getPestanaActiva() { return pestanaActiva; }
    public void setPestanaActiva(int pestana) { pestanaActiva = pestana; }

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    @Override
    protected DAOInterface<Procedimiento, UUID> getDAO() {
        return procedimientoDAO;
    }

    /**
     * Prepara un procedimiento con el identificador requerido por JPA.
     *
     * @return procedimiento nuevo con UUID asignado
     */
    @Override
    protected Procedimiento crearNuevoRegistro() {
        Procedimiento nuevo = new Procedimiento(UUID.randomUUID());
        nuevo.setActivo(true);
        return nuevo;
    }

    public ProcedimientoDAO getProcedimientoDAO() {
        return procedimientoDAO;
    }

    public void setProcedimientoDAO(ProcedimientoDAO procedimientoDAO) {
        this.procedimientoDAO = procedimientoDAO;
    }

    public List<Procedimiento> getProcedimientosActivos() {
        return procedimientoDAO.findAllActivos();
    }
}
