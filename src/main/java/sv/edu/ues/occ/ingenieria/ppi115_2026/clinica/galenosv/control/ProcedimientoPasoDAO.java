package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPaso;

/**
 * Acceso a los pasos de procedimientos. Carga el procedimiento y el rol
 * asociados cuando lista pasos para el catálogo.
 */
@ApplicationScoped
public class ProcedimientoPasoDAO
        extends DefaultDAO<ProcedimientoPaso, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ProcedimientoPasoDAO() {
        super(ProcedimientoPaso.class);
    }

    public ProcedimientoPasoDAO(EntityManager em) {
        super(ProcedimientoPaso.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Lista los pasos con los datos necesarios para mostrar los nombres
     * de su procedimiento y rol en la tabla JSF.
     *
     * @return pasos con sus relaciones cargadas
     */
    @Override
    public List<ProcedimientoPaso> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProcedimientoPaso> cq =
                cb.createQuery(ProcedimientoPaso.class);
        Root<ProcedimientoPaso> paso =
                cq.from(ProcedimientoPaso.class);

        paso.fetch("idProcedimiento", JoinType.LEFT);
        paso.fetch("idRol", JoinType.LEFT);
        cq.select(paso);

        return em.createQuery(cq).getResultList();
    }
    /** Carga los pasos, roles y exámenes del procedimiento sin consultas por fila. */
    public List<ProcedimientoPaso> findByProcedimiento(UUID idProcedimiento) {
        return em.createQuery("SELECT DISTINCT p FROM ProcedimientoPaso p "
                + "LEFT JOIN FETCH p.idProcedimiento LEFT JOIN FETCH p.idRol "
                + "LEFT JOIN FETCH p.procedimientoPasoExamenList a "
                + "LEFT JOIN FETCH a.idExamen "
                + "WHERE p.idProcedimiento.idProcedimiento = :id", ProcedimientoPaso.class)
                .setParameter("id", idProcedimiento).getResultList();
    }
    public boolean tieneFin(UUID idProcedimiento) {
        if (idProcedimiento == null) return false;
        String jpql = "SELECT COUNT(p) FROM ProcedimientoPaso p WHERE p.idProcedimiento.idProcedimiento = :idProc AND p.indicaFin = true";
        Long count = getEntityManager().createQuery(jpql, Long.class)
                .setParameter("idProc", idProcedimiento)
                .getSingleResult();
        return count != null && count > 0;
    }

    public ProcedimientoPaso findPasoInicial(UUID idProcedimiento) {
        if (idProcedimiento == null) return null;
        return getEntityManager().createQuery(
                "SELECT pp FROM ProcedimientoPaso pp LEFT JOIN FETCH pp.idRol"
                + " WHERE pp.idProcedimiento.idProcedimiento = :idProc"
                + " AND NOT EXISTS (SELECT s FROM ProcedimientoPasoSecuencia s"
                + " WHERE s.idProcedimientoPaso.idProcedimiento.idProcedimiento = :idProc"
                + " AND s.idProcedimientoPasoReferencia = pp.idProcedimientoPaso)"
                + " ORDER BY pp.nombre, pp.idProcedimientoPaso", ProcedimientoPaso.class)
                .setParameter("idProc", idProcedimiento).setMaxResults(1)
                .getResultList().stream().findFirst().orElse(null);
    }

}
