package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.UUID;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;

/**
 * Acceso a datos para la entidad ExamenResultado.
 */
@ApplicationScoped
public class ExamenResultadoDAO extends DefaultDAO<ExamenResultado, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ExamenResultadoDAO() {
        super(ExamenResultado.class);
    }

    public ExamenResultadoDAO(EntityManager em) {
        super(ExamenResultado.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("resultado", "interpretacion");
    }


    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idOrdenExamen.idConsultaProcedimientoPaso.idConsultaProcedimiento.idProcedimiento");
    }
    
   public List<ExamenResultado> findByOrdenExamen(UUID idOrdenExamen) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExamenResultado> cq = cb.createQuery(ExamenResultado.class);
        Root<ExamenResultado> root = cq.from(ExamenResultado.class);
        cq.select(root)
                .where(cb.equal(root.get("idOrdenExamen").get("idOrdenExamen"), idOrdenExamen))
                .orderBy(cb.asc(root.get("fechaCreacion")));
        return em.createQuery(cq).getResultList();
    }
}
