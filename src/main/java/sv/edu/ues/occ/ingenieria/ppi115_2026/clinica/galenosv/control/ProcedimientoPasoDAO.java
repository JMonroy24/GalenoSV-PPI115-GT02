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
     * Lista los pasos con los datos necesarios para mostrar los nombres
     * de su procedimiento y rol en la tabla JSF.
     *
     * @return pasos con sus relaciones cargadas
     */
    @Override
    public List<ProcedimientoPaso> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProcedimientoPaso> cq =
                cb.createQuery(ProcedimientoPaso.class);
        Root<ProcedimientoPaso> paso =
                cq.from(ProcedimientoPaso.class);

        paso.fetch("idProcedimiento", JoinType.LEFT);
        paso.fetch("idRol", JoinType.LEFT);
        cq.select(paso);

        return em.createQuery(cq).getResultList();
    }

    public java.util.List<ProcedimientoPaso> buscarParaAutocompletar(String filtro, int max) {
        String patron = "%" + (filtro == null ? "" : filtro.trim().toLowerCase()) + "%";
        return getEntityManager().createQuery(
                "SELECT e FROM ProcedimientoPaso e",
                ProcedimientoPaso.class)
                .setMaxResults(max)
                .getResultList();
    }
}
