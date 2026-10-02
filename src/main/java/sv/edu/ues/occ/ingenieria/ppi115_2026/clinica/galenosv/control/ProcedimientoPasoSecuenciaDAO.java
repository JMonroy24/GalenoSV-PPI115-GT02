package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ProcedimientoPasoSecuencia;

/**
 * Acceso a las relaciones de secuencia entre pasos de un procedimiento.
 * Cada relación parte de un paso y guarda el UUID del paso de referencia.
 */
@ApplicationScoped
public class ProcedimientoPasoSecuenciaDAO
        extends DefaultDAO<ProcedimientoPasoSecuencia, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ProcedimientoPasoSecuenciaDAO() {
        super(ProcedimientoPasoSecuencia.class);
    }

    public ProcedimientoPasoSecuenciaDAO(EntityManager em) {
        super(ProcedimientoPasoSecuencia.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected java.util.List<String> getCamposBusqueda() {
        return java.util.List.of("tipoSecuencia");
    }


    /**
     * Consulta las relaciones de secuencia que parten del paso indicado.
     *
     * @param idPaso identificador del paso de origen
     * @return relaciones de secuencia del paso
     */
    public List<ProcedimientoPasoSecuencia> findByPaso(UUID idPaso) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProcedimientoPasoSecuencia> cq =
                cb.createQuery(ProcedimientoPasoSecuencia.class);
        Root<ProcedimientoPasoSecuencia> secuencia =
                cq.from(ProcedimientoPasoSecuencia.class);

        cq.select(secuencia)
                .where(cb.equal(
                        secuencia.get("idProcedimientoPaso")
                                .get("idProcedimientoPaso"),
                        idPaso
                ));

        return em.createQuery(cq).getResultList();
    }


    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idProcedimientoPaso.idProcedimiento");
    }

    public boolean existeSecuencia(UUID idPaso, UUID idReferencia, String tipo) {
        return existeSecuencia(idPaso, idReferencia, tipo, null);
    }

    public boolean existeSecuencia(UUID idPaso, UUID idReferencia, String tipo, UUID excluirId) {
        if (idPaso == null || idReferencia == null || tipo == null || tipo.isBlank()) return false;
        String jpql = "SELECT COUNT(s) FROM ProcedimientoPasoSecuencia s"
                + " WHERE s.idProcedimientoPaso.idProcedimientoPaso = :paso"
                + " AND s.idProcedimientoPasoReferencia = :referencia AND LOWER(s.tipoSecuencia) = :tipo"
                + (excluirId == null ? "" : " AND s.idProcedimientoPasoSecuencia <> :excluir");
        var query = getEntityManager().createQuery(jpql, Long.class).setParameter("paso", idPaso)
                .setParameter("referencia", idReferencia).setParameter("tipo", tipo.trim().toLowerCase(java.util.Locale.ROOT));
        if (excluirId != null) query.setParameter("excluir", excluirId);
        return query.getSingleResult() > 0;
    }

    /** Grafo completo del procedimiento para validar ciclos dentro de una transacción. */
    public List<ProcedimientoPasoSecuencia> findByProcedimiento(UUID idProcedimiento) {
        if (idProcedimiento == null) return List.of();
        return getEntityManager().createQuery("SELECT s FROM ProcedimientoPasoSecuencia s"
                + " JOIN FETCH s.idProcedimientoPaso p WHERE p.idProcedimiento.idProcedimiento = :procedimiento"
                + " ORDER BY s.idProcedimientoPasoSecuencia", ProcedimientoPasoSecuencia.class)
                .setParameter("procedimiento", idProcedimiento).getResultList();
    }
}
