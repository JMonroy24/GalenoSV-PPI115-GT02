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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ExamenResultadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.OrdenExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.OrdenExamen;

/**
 * Backing bean de OrdenExamen. Administra embebidos sus resultados (ExamenResultado).
 */
@Named("ordenExamenModel")
@ViewScoped
public class OrdenExamenModel extends ModelTransaccional<OrdenExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected OrdenExamenDAO ordenExamenDAO;
    @Inject
    protected ConsultaProcedimientoPasoDAO consultaProcedimientoPasoDAO;
    @Inject
    protected ExamenResultadoDAO examenResultadoDAO;

    private List<ExamenResultado> resultados = Collections.emptyList();
    private ExamenResultado resultadoNuevo;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
        resultadoNuevo = nuevoResultado();
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
        if (query == null || query.trim().length() < 2) return java.util.List.of();
        return consultaProcedimientoPasoDAO.buscarParaAutocompletar(query, 20);
    }

    private ExamenResultado nuevoResultado() {
        return new ExamenResultado(UUID.randomUUID());
    }


    @Override
    public void seleccionar(OrdenExamen orden) {
        super.seleccionar(orden);
        resultados = (List<ExamenResultado>) examenResultadoDAO.findByOrdenExamen(orden.getIdOrdenExamen());
        resultadoNuevo = nuevoResultado();
    }

    @Override
    public void prepararNuevo() {
        super.prepararNuevo();
        resultados = Collections.emptyList();
        resultadoNuevo = nuevoResultado();
    }

    @Override
    public void cancelar() {
        super.cancelar();
        resultados = Collections.emptyList();
        resultadoNuevo = nuevoResultado();
    }


    public void agregarResultado() {
        if (!relacionesHabilitadas("Resultado")) {
            return;
        }
        if (resultadoNuevo.getResultado() == null || resultadoNuevo.getResultado().isBlank()
                || resultadoNuevo.getInterpretacion() == null
                || resultadoNuevo.getInterpretacion().isBlank()) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Resultado",
                    "El resultado y la interpretación son obligatorios.");
            return;
        }
        resultadoNuevo.setIdOrdenExamen(getRegistroActual());
        resultadoNuevo.setFechaCreacion(new Date());
        ejecutarRelacion("Resultado", () -> {
            examenResultadoDAO.create(resultadoNuevo);
            resultados = (List<ExamenResultado>) examenResultadoDAO.findByOrdenExamen(
                    getRegistroActual().getIdOrdenExamen());
            resultadoNuevo = nuevoResultado();
        }, "Resultado agregado correctamente.");
    }

    public void quitarResultado(ExamenResultado resultado) {
        if (!relacionesHabilitadas("Resultado")) {
            return;
        }
        if (resultado == null || !resultados.contains(resultado)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Resultado",
                    "Seleccione un resultado válido de la orden.");
            return;
        }
        ejecutarRelacion("Resultado", () -> {
            examenResultadoDAO.delete(resultado);
            resultados = (List<ExamenResultado>) examenResultadoDAO.findByOrdenExamen(
                    getRegistroActual().getIdOrdenExamen());
        }, "Resultado quitado correctamente.");
    }

    public List<ExamenResultado> getResultados() { return resultados; }
    public ExamenResultado getResultadoNuevo() { return resultadoNuevo; }

    public OrdenExamenDAO getOrdenExamenDAO() {
        return ordenExamenDAO;
    }

    public void setOrdenExamenDAO(OrdenExamenDAO ordenExamenDAO) {
        this.ordenExamenDAO = ordenExamenDAO;
    }
    @Override
    protected void validarNegocio(OrdenExamen registro) {
        registro.setIndicaciones(ValidadorComun.textoObligatorio(registro.getIndicaciones(), "Las indicaciones"));
        ValidadorComun.requerido(registro.getIdConsultaProcedimientoPaso(), "Seleccione un paso de la consulta.");
    }

}
