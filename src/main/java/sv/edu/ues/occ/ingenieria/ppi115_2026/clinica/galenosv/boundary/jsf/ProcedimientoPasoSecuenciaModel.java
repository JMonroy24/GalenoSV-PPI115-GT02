package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control.ProcedimientoPasoSecuenciaDAO;
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
        extends Model<ProcedimientoPasoSecuencia, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * DAO utilizado para consultar y modificar las relaciones de secuencia.
     */
    @Inject
    protected ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO;

    /**
     * Carga las relaciones existentes cuando JSF construye el modelo.
     */
    @PostConstruct
    public void init() {
        cargarDatos();
    }

    /**
     * Proporciona al modelo genérico el DAO encargado de esta entidad.
     *
     * @return DAO de las relaciones entre pasos
     */
    @Override
    protected DAOInterface<ProcedimientoPasoSecuencia, UUID> getDAO() {
        return procedimientoPasoSecuenciaDAO;
    }

    /**
     * Crea una relación vacía que podrá ser completada desde el formulario.
     *
     * @return nueva instancia de ProcedimientoPasoSecuencia
     */
    @Override
    protected ProcedimientoPasoSecuencia crearNuevoRegistro() {
        return new ProcedimientoPasoSecuencia();
    }

    /**
     * Obtiene el DAO asociado al modelo.
     *
     * @return DAO utilizado por el modelo
     */
    public ProcedimientoPasoSecuenciaDAO
            getProcedimientoPasoSecuenciaDAO() {
        return procedimientoPasoSecuenciaDAO;
    }

    /**
     * Permite proporcionar el DAO utilizado por el modelo.
     *
     * Este método también permite inyectar un DAO simulado durante las pruebas
     * unitarias.
     *
     * @param procedimientoPasoSecuenciaDAO DAO que utilizará el modelo
     */
    public void setProcedimientoPasoSecuenciaDAO(
            ProcedimientoPasoSecuenciaDAO procedimientoPasoSecuenciaDAO) {
        this.procedimientoPasoSecuenciaDAO =
                procedimientoPasoSecuenciaDAO;
    }
}