package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidacionNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ValidadorComun;
import jakarta.faces.application.FacesMessage;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

/**
 * Administra el catálogo de procedimientos clínicos.
 * La gestión de los pasos se realiza desde ProcedimientoPasoModel.
 */
@Named("procedimientoModel")
@ViewScoped
public class ProcedimientoModel extends ModelTransaccional<Procedimiento, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoDAO procedimientoDAO;

    @Inject
    protected ProcedimientoPasoModel procedimientoPasoModel;

    @Inject
    protected sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO procedimientoPasoDAO;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
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
        Procedimiento registro = new Procedimiento(UUID.randomUUID());
        registro.setActivo(true);
        return registro;
    }

    /**
     * Fuente de opciones para selectores de FK en otras vistas (p.ej. ProcedimientoPaso).
     * Retorna solo los procedimientos activos sin cargar el modelo completo.
     *
     * @return lista de Procedimiento ordenada por nombre
     */
    public java.util.List<Procedimiento> getProcedimientosActivos() {
        return procedimientoDAO.findAllActivos();
    }

    public ProcedimientoDAO getProcedimientoDAO() {
        return procedimientoDAO;
    }

    public void setProcedimientoDAO(ProcedimientoDAO procedimientoDAO) {
        this.procedimientoDAO = procedimientoDAO;
    }

    public ProcedimientoPasoModel getProcedimientoPasoModel() {
        return procedimientoPasoModel;
    }

    public void setProcedimientoPasoModel(ProcedimientoPasoModel procedimientoPasoModel) {
        this.procedimientoPasoModel = procedimientoPasoModel;
    }

    public sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO getProcedimientoPasoDAO() {
        return procedimientoPasoDAO;
    }

    public void setProcedimientoPasoDAO(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoDAO procedimientoPasoDAO) {
        this.procedimientoPasoDAO = procedimientoPasoDAO;
    }

    @Override
    public void seleccionar(Procedimiento registro) {
        super.seleccionar(registro);
        if (registro != null) {
            procedimientoPasoModel.prepararNuevo();
            procedimientoPasoModel.getRegistroActual().setIdProcedimiento(registro);
            
            if (!procedimientoPasoDAO.tieneFin(registro.getIdProcedimiento())) {
                agregarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Este procedimiento no tiene ningún paso marcado como fin.");
            }
        }
    }

    public java.util.List<sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso> getPasos() {
        if (getRegistroActual() == null || getRegistroActual().getIdProcedimiento() == null) {
            return java.util.Collections.emptyList();
        }
        return procedimientoPasoDAO.findByProcedimiento(getRegistroActual().getIdProcedimiento(), null);
    }

    @Override
    protected void validarNegocio(Procedimiento registro) {
        registro.setNombre(ValidadorComun.textoObligatorio(registro.getNombre(), "El nombre"));
        if (procedimientoDAO.existePorCampo("nombre", registro.getNombre(), registro.getIdProcedimiento())) {
            throw new ValidacionNegocioException("Ya existe un registro con este nombre.");
        }
    }

}
