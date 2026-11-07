package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

/**
 * Modelo JSF encargado de administrar las relaciones de secuencia entre los
 * pasos que forman parte de un procedimiento.
 *
 * Cada registro establece un paso, un paso de referencia y el tipo de
 * secuencia que los relaciona. El modelo mantiene el estado de la vista y
 * delega las operaciones de persistencia a
 * .
 */
@Named("procedimientoPasoSecuenciaModel")
@ViewScoped
public class ProcedimientoPasoSecuenciaModel
        extends ModelTransaccional<ProcedimientoPasoSecuencia, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * DAO utilizado para consultar y modificar las relaciones de secuencia.
     */
    @Inject
    protected ProcedimientoPasoSecuenciaService procedimientoPasoSecuenciaService;

    /**
     * Carga las relaciones existentes cuando JSF construye el modelo.
     */
    @PostConstruct
    public void init() {
        inicializarLazyModel();
    }

    /**
     * Proporciona al modelo genérico el DAO encargado de esta entidad.
     *
     * @return DAO de las relaciones entre pasos
     */
    @Override
    protected DAOInterface<ProcedimientoPasoSecuencia, UUID> getDAO() {
        return procedimientoPasoSecuenciaService;
    }

    /**
     * Crea una relación vacía que podrá ser completada desde el formulario.
     *
     * @return nueva instancia de ProcedimientoPasoSecuencia
     */
    @Override
    protected ProcedimientoPasoSecuencia crearNuevoRegistro() {
        return new ProcedimientoPasoSecuencia(UUID.randomUUID());
    }

    /**
     * Obtiene el DAO asociado al modelo.
     *
     * @return DAO utilizado por el modelo
     */
    public ProcedimientoPasoSecuenciaService
            getProcedimientoPasoSecuenciaService() {
        return procedimientoPasoSecuenciaService;
    }

    /**
     * Permite proporcionar el DAO utilizado por el modelo.
     *
     * Este método también permite inyectar un DAO simulado durante las pruebas
     * unitarias.
     *
     * @param procedimientoPasoSecuenciaService DAO que utilizará el modelo
     */
    public void setProcedimientoPasoSecuenciaService(
            ProcedimientoPasoSecuenciaService procedimientoPasoSecuenciaService) {
        this.procedimientoPasoSecuenciaService =
                procedimientoPasoSecuenciaService;
    }

    @Override
    protected void persistirNuevo(ProcedimientoPasoSecuencia registro) {
        procedimientoPasoSecuenciaService.crear(registro);
    }

    @Override
    protected void persistirCambios(ProcedimientoPasoSecuencia registro) {
        procedimientoPasoSecuenciaService.actualizar(registro);
    }

}
