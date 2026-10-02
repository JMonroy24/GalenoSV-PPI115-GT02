package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
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
}
