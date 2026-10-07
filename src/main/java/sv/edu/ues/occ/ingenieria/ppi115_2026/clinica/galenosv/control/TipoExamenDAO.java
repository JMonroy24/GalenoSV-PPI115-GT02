package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.TipoExamen;

/**
 * Acceso a datos para la entidad TipoExamen.
 */
@ApplicationScoped
public class TipoExamenDAO extends DefaultDAO<TipoExamen, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public TipoExamenDAO() {
        super(TipoExamen.class);
    }

    public TipoExamenDAO(EntityManager em) {
        super(TipoExamen.class);
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
     * Devuelve todos los tipos de examen activos, para alimentar selectores de FK
     * en formularios. Consulta específica sobre catálogo pequeño.
     *
     * @return lista de TipoExamen con activo = true, ordenados por nombre
     */
    public java.util.List<TipoExamen> findAllActivos() {
        return getEntityManager().createQuery(
                "SELECT t FROM TipoExamen t WHERE t.activo = true ORDER BY t.nombre",
                TipoExamen.class)
                .getResultList();
    }
}
