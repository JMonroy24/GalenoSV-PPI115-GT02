package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

/**
 * Componente de acceso a datos para las relaciones de secuencia existentes
 * entre los pasos de un procedimiento.
 *
 * Cada registro administrado identifica un paso, un paso de referencia y el
 * tipo de secuencia que los relaciona. Las operaciones de creación, consulta,
 * modificación y eliminación son heredadas de {@link DefaultDAO}, utilizando
 * un {@link UUID} como identificador.
 */
@ApplicationScoped
public class ProcedimientoPasoSecuenciaDAO
        extends DefaultDAO<ProcedimientoPasoSecuencia, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Administrador de entidades proporcionado por la unidad de persistencia
     * del proyecto.
     */
    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    /**
     * Constructor requerido por CDI.
     *
     * Configura en el DAO genérico la entidad que será administrada.
     */
    public ProcedimientoPasoSecuenciaDAO() {
        super(ProcedimientoPasoSecuencia.class);
    }

    /**
     * Constructor utilizado por las pruebas unitarias para proporcionar un
     * EntityManager simulado.
     *
     * @param em administrador de entidades utilizado por las operaciones CRUD
     */
    public ProcedimientoPasoSecuenciaDAO(EntityManager em) {
        super(ProcedimientoPasoSecuencia.class);
        this.em = em;
    }

    /**
     * Proporciona el EntityManager utilizado por las operaciones heredadas de
     * DefaultDAO.
     *
     * @return administrador de entidades asociado a GalenoSV_PU
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}