package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoDocumento;

/**
 * Acceso a datos para la entidad TipoDocumento.
 */
@ApplicationScoped
public class TipoDocumentoDAO extends DefaultDAO<TipoDocumento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public TipoDocumentoDAO() {
        super(TipoDocumento.class);
    }

    public TipoDocumentoDAO(EntityManager em) {
        super(TipoDocumento.class);
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



    /** Catálogo activo, ordenado para los selectores de los formularios. */
    public java.util.List<TipoDocumento> findAllActivos() {
        return getEntityManager().createQuery(
                "SELECT e FROM TipoDocumento e WHERE e.activo = true ORDER BY e.nombre, e.idTipoDocumento",
                TipoDocumento.class).getResultList();
    }
}
