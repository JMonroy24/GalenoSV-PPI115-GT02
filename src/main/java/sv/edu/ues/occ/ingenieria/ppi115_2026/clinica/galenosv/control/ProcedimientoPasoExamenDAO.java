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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoExamen;

/**
 * Acceso a las asociaciones entre pasos de procedimiento y exámenes.
 * Permite consultar las asociaciones de un paso con el examen cargado.
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

    @Override
    protected java.util.List<String> getCamposBusqueda() {
        return java.util.List.of("observaciones");
    }


    /**
     * Consulta los exámenes asociados al paso indicado.
     *
     * @param idPaso identificador del paso
     * @return asociaciones del paso con sus exámenes cargados
     */
    public List<ProcedimientoPasoExamen> findByPaso(UUID idPaso) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProcedimientoPasoExamen> cq =
                cb.createQuery(ProcedimientoPasoExamen.class);
        Root<ProcedimientoPasoExamen> asociacion =
                cq.from(ProcedimientoPasoExamen.class);

        asociacion.fetch("idExamen", JoinType.LEFT);
        cq.select(asociacion)
                .where(cb.equal(
                        asociacion.get("idProcedimientoPaso")
                                .get("idProcedimientoPaso"),
                        idPaso
                ));

        return em.createQuery(cq).getResultList();
    }


    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idProcedimientoPaso.idProcedimiento", "idExamen");
    }

    public boolean existePasoExamen(UUID idPaso, UUID idExamen) {
        return existePasoExamen(idPaso, idExamen, null);
    }

    public boolean existePasoExamen(UUID idPaso, UUID idExamen, UUID excluirId) {
        if (idPaso == null || idExamen == null) return false;
        String jpql = "SELECT COUNT(p) FROM ProcedimientoPasoExamen p"
                + " WHERE p.idProcedimientoPaso.idProcedimientoPaso = :paso AND p.idExamen.idExamen = :examen"
                + (excluirId == null ? "" : " AND p.idProcedimientoPasoExamen <> :excluir");
        var query = getEntityManager().createQuery(jpql, Long.class)
                .setParameter("paso", idPaso).setParameter("examen", idExamen);
        if (excluirId != null) query.setParameter("excluir", excluirId);
        return query.getSingleResult() > 0;
    }
}
