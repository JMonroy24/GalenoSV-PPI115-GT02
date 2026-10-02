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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

/**
 * Acceso a datos para la entidad MedioContacto.
 */
@ApplicationScoped
public class MedioContactoDAO extends DefaultDAO<MedioContacto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public MedioContactoDAO() {
        super(MedioContacto.class);
    }

    public MedioContactoDAO(EntityManager em) {
        super(MedioContacto.class);
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
        return java.util.List.of("idPersona", "idTipoMedioContacto");
    }

    public boolean existePersonaTipoValor(UUID idPersona, UUID idTipo, String valor, UUID excluirId) {
        if (idPersona == null || idTipo == null || valor == null || valor.isBlank()) return false;
        String jpql = "SELECT COUNT(m) FROM MedioContacto m"
                + " WHERE m.idPersona.idPersona = :persona AND m.idTipoMedioContacto.idTipoMedioContacto = :tipo"
                + " AND LOWER(m.valor) = :valor" + (excluirId == null ? "" : " AND m.idMedioContacto <> :excluir");
        var query = getEntityManager().createQuery(jpql, Long.class).setParameter("persona", idPersona)
                .setParameter("tipo", idTipo).setParameter("valor", valor.trim().toLowerCase(java.util.Locale.ROOT));
        if (excluirId != null) query.setParameter("excluir", excluirId);
        return query.getSingleResult() > 0;
    }
    
    public List<MedioContacto> findByPersona(UUID idPersona) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MedioContacto> cq = cb.createQuery(MedioContacto.class);
        Root<MedioContacto> root = cq.from(MedioContacto.class);
        root.fetch("idTipoMedioContacto", JoinType.LEFT);
        cq.select(root).where(
                cb.equal(root.get("idPersona").get("idPersona"), idPersona));
        return em.createQuery(cq).getResultList();
    }
}
