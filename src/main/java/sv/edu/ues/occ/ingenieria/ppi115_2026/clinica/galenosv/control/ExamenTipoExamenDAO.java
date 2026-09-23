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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

/**
 * Acceso a las asociaciones entre exámenes y tipos de examen.
 */
@ApplicationScoped
public class ExamenTipoExamenDAO
        extends DefaultDAO<ExamenTipoExamen, UUID>
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ExamenTipoExamenDAO() {
        super(ExamenTipoExamen.class);
    }

    public ExamenTipoExamenDAO(EntityManager em) {
        super(ExamenTipoExamen.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Obtiene los tipos asignados a un examen y carga el tipo necesario
     * para mostrar su nombre en la vista.
     *
     * @param idExamen identificador del examen seleccionado
     * @return asociaciones pertenecientes al examen
     */
    public List<ExamenTipoExamen> findByExamen(UUID idExamen) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExamenTipoExamen> cq =
                cb.createQuery(ExamenTipoExamen.class);
        Root<ExamenTipoExamen> asociacion =
                cq.from(ExamenTipoExamen.class);

        asociacion.fetch("idTipoExamen", JoinType.LEFT);
        cq.select(asociacion)
                .where(cb.equal(
                        asociacion.get("idExamen").get("idExamen"),
                        idExamen
                ));

        return em.createQuery(cq).getResultList();
    }
}