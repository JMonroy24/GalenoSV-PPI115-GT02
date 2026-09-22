package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

/**
 * Componente de acceso a datos para las asociaciones entre los pasos de un
 * procedimiento y los exámenes clínicos que deben realizarse en dichos pasos.
 *
 * Cada registro administrado representa un examen requerido por un paso
 * específico. Las operaciones de creación, consulta, modificación y
 * eliminación son heredadas de , utilizando un
 *  como identificador.
 */
@ApplicationScoped
public class ProcedimientoPasoExamenDAO
        extends DefaultDAO<ProcedimientoPasoExamen, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ProcedimientoPasoExamenDAO() {
        super(ProcedimientoPasoExamen.class);
    }

    public ProcedimientoPasoExamenDAO(EntityManager em) {
        super(ProcedimientoPasoExamen.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}