package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

/**
 * Modelo JSF encargado de administrar las asociaciones entre los pasos de los
 * procedimientos y los exámenes clínicos.
 *
 * Mantiene el estado de la vista mientras el usuario consulta o modifica una
 * asociación y delega las operaciones de persistencia a
 * . Cada registro indica qué examen
 * corresponde a un paso determinado dentro de un procedimiento.
 */
@Named("procedimientoPasoExamenModel")
@ViewScoped
public class ProcedimientoPasoExamenModel
        extends ModelTransaccional<ProcedimientoPasoExamen, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected ProcedimientoPasoExamenService procedimientoPasoExamenService;

    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    @Override
    protected DAOInterface<ProcedimientoPasoExamen, UUID> getDAO() {
        return procedimientoPasoExamenService;
    }

    @Override
    protected ProcedimientoPasoExamen crearNuevoRegistro() {
        return new ProcedimientoPasoExamen(UUID.randomUUID());
    }

    public ProcedimientoPasoExamenService getProcedimientoPasoExamenService() {
        return procedimientoPasoExamenService;
    }

    public void setProcedimientoPasoExamenService(
            ProcedimientoPasoExamenService procedimientoPasoExamenService) {
        this.procedimientoPasoExamenService = procedimientoPasoExamenService;
    }

    @Override
    protected void persistirNuevo(ProcedimientoPasoExamen registro) {
        procedimientoPasoExamenService.crear(registro);
    }

    @Override
    protected void persistirCambios(ProcedimientoPasoExamen registro) {
        procedimientoPasoExamenService.actualizar(registro);
    }

}
