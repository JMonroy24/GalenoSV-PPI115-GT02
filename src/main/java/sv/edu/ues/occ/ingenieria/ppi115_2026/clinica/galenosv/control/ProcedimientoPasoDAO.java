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

    @Override
    protected java.util.List<String> getCamposBusqueda() {
        return java.util.List.of("nombre");
    }




    /**
     * Devuelve los pasos del procedimiento indicado, excluyendo el paso cuyo
     * UUID se pase como {@code excluir} (para el caso de secuencias: no se puede
     * crear una secuencia hacia sí mismo).
     *
     * @param idProcedimiento UUID del procedimiento padre
     * @param excluir         UUID del paso que se excluye de la lista (puede ser null)
     * @return lista de pasos del mismo procedimiento, sin el excluido
     */
    public java.util.List<ProcedimientoPaso> findByProcedimiento(
            UUID idProcedimiento, UUID excluir) {
        if (idProcedimiento == null) {
            return java.util.Collections.emptyList();
        }
        String jpql = "SELECT pp FROM ProcedimientoPaso pp"
                + " LEFT JOIN FETCH pp.idProcedimiento LEFT JOIN FETCH pp.idRol"
                + " WHERE pp.idProcedimiento.idProcedimiento = :idProc"
                + (excluir != null ? " AND pp.idProcedimientoPaso <> :excluir" : "")
                + " ORDER BY pp.nombre";
        var query = getEntityManager().createQuery(jpql, ProcedimientoPaso.class)
                .setParameter("idProc", idProcedimiento);
        if (excluir != null) {
            query.setParameter("excluir", excluir);
        }
        return query.getResultList();
    }

    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idProcedimiento", "idRol");
    }

    public boolean existeNombreEnProcedimiento(UUID idProcedimiento, String nombre, UUID excluirId) {
        if (idProcedimiento == null || nombre == null || nombre.isBlank()) return false;
        String jpql = "SELECT COUNT(p) FROM ProcedimientoPaso p"
                + " WHERE p.idProcedimiento.idProcedimiento = :procedimiento AND LOWER(p.nombre) = :nombre"
                + (excluirId == null ? "" : " AND p.idProcedimientoPaso <> :excluir");
        var query = getEntityManager().createQuery(jpql, Long.class).setParameter("procedimiento", idProcedimiento)
                .setParameter("nombre", nombre.trim().toLowerCase(java.util.Locale.ROOT));
        if (excluirId != null) query.setParameter("excluir", excluirId);
        return query.getSingleResult() > 0;
    }

    /**
     * SIGUIENTE representa origen -> referencia. El inicio no aparece como
     * referencia de ninguna secuencia del procedimiento. Si hay varios pasos
     * desconectados se elige por nombre e identificador, de forma determinista.
     */
    
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
