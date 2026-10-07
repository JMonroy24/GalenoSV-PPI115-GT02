package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Procedimiento;

/**
 * Acceso a datos para la entidad Procedimiento.
 *
 * Hereda las operaciones CRUD de DefaultDAO.
 */
@ApplicationScoped
public class ProcedimientoDAO
        extends DefaultDAO<Procedimiento, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ProcedimientoDAO() {
        super(Procedimiento.class);
    }

    public ProcedimientoDAO(EntityManager em) {
        super(Procedimiento.class);
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
     * Devuelve todos los procedimientos disponibles, para alimentar selectores de FK
     * en formularios. Consulta específica sobre catálogo pequeño.
     *
     * @return lista de Procedimiento ordenada por nombre
     */
    public java.util.List<Procedimiento> findAllActivos() {
        return getEntityManager().createQuery(
                "SELECT p FROM Procedimiento p WHERE p.activo = true ORDER BY p.nombre",
                Procedimiento.class)
                .getResultList();
    }
}
