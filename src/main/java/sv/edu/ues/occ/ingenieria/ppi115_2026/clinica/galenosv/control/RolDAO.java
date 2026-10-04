package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Rol;

/**
 * Acceso a datos para la entidad Rol.
 */
@ApplicationScoped
public class RolDAO extends DefaultDAO<Rol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public RolDAO() {
        super(Rol.class);
    }

    public RolDAO(EntityManager em) {
        super(Rol.class);
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
     * Devuelve todos los roles disponibles, para alimentar selectores de FK
     * en formularios. Consulta específica sobre catálogo pequeño.
     *
     * @return lista de Rol ordenada por nombre
     */
    public java.util.List<Rol> findAllActivos() {
        return getEntityManager().createQuery(
                "SELECT r FROM Rol r WHERE r.activo = true ORDER BY r.nombre",
                Rol.class)
                .getResultList();
    }
}
