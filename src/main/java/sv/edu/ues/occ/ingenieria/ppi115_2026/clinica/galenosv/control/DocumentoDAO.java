package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import jakarta.persistence.EntityManager;
import java.util.List;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;
import java.util.List;

/**
 * Acceso a datos para la entidad .
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

    public java.util.List<Documento> buscarParaAutocompletar(String filtro, int max) {
        String patron = "%" + (filtro == null ? "" : filtro.trim().toLowerCase()) + "%";
        return getEntityManager().createQuery(
                "SELECT e FROM Documento e",
                Documento.class)
                .setMaxResults(max)
                .getResultList();
    }
}
