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
    protected List<String> getCamposBusqueda() {
        return List.of("nombre", "idRol.nombre", "idProcedimiento.nombre");
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
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
    /** Carga los pasos, roles y exámenes del procedimiento sin consultas por fila. */
    public List<ProcedimientoPaso> findByProcedimiento(UUID idProcedimiento) {
        return em.createNamedQuery("ProcedimientoPaso.findByProcedimiento", ProcedimientoPaso.class)
                .setParameter("id", idProcedimiento)
                // La asociación se guarda desde el lado del examen. Refrescar la colección inversa.
                .setHint("eclipselink.refresh", true)
                .getResultList();
    }
    public boolean tieneFin(UUID idProcedimiento) {
        if (idProcedimiento == null) return false;

        Long count = getEntityManager().createNamedQuery("ProcedimientoPaso.tieneFin", Long.class)
                .setParameter("idProc", idProcedimiento)
                .getSingleResult();
        return count != null && count > 0;
    }

    public ProcedimientoPaso findPasoInicial(UUID idProcedimiento) {
        if (idProcedimiento == null) return null;
        return getEntityManager().createNamedQuery("ProcedimientoPaso.findPasoInicial", ProcedimientoPaso.class)
                .setParameter("idProc", idProcedimiento).setMaxResults(1)
                .getResultList().stream().findFirst().orElse(null);
    }

}
