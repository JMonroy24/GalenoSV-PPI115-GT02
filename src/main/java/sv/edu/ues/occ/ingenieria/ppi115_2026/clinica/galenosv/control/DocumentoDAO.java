package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.UUID;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;

/**
 * Acceso a datos para la entidad Documento.
 */
@ApplicationScoped
public class DocumentoDAO extends DefaultDAO<Documento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public DocumentoDAO() {
        super(Documento.class);
    }

    public DocumentoDAO(EntityManager em) {
        super(Documento.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("valor");
    }


    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idPersona", "idTipoDocumento");
    }

    public boolean existeTipoValor(UUID idTipo, String valor, UUID excluirId) {
        if (idTipo == null || valor == null || valor.isBlank()) return false;
        String jpql = "SELECT COUNT(d) FROM Documento d"
                + " WHERE d.idTipoDocumento.idTipoDocumento = :tipo AND LOWER(d.valor) = :valor"
                + (excluirId == null ? "" : " AND d.idDocumento <> :excluir");
        var query = getEntityManager().createQuery(jpql, Long.class)
                .setParameter("tipo", idTipo).setParameter("valor", valor.trim().toLowerCase(java.util.Locale.ROOT));
        if (excluirId != null) query.setParameter("excluir", excluirId);
        return query.getSingleResult() > 0;
    }

    /** Solo impide repetir el mismo documento dentro de una persona. */
    public boolean existePersonaTipoValor(UUID idPersona, UUID idTipo, String valor, UUID excluirId) {
        if (idPersona == null || idTipo == null || valor == null || valor.isBlank()) return false;
        String jpql = "SELECT COUNT(d) FROM Documento d"
                + " WHERE d.idPersona.idPersona = :persona AND d.idTipoDocumento.idTipoDocumento = :tipo"
                + " AND LOWER(d.valor) = :valor" + (excluirId == null ? "" : " AND d.idDocumento <> :excluir");
        var query = getEntityManager().createQuery(jpql, Long.class).setParameter("persona", idPersona)
                .setParameter("tipo", idTipo).setParameter("valor", valor.trim().toLowerCase(java.util.Locale.ROOT));
        if (excluirId != null) query.setParameter("excluir", excluirId);
        return query.getSingleResult() > 0;
    }
    
    public List<Documento> findByPersona(UUID idPersona) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Documento> cq = cb.createQuery(Documento.class);
        Root<Documento> root = cq.from(Documento.class);
        root.fetch("idTipoDocumento", JoinType.LEFT);
        cq.select(root).where(
                cb.equal(root.get("idPersona").get("idPersona"), idPersona));
        return em.createQuery(cq).getResultList();
    }
}
