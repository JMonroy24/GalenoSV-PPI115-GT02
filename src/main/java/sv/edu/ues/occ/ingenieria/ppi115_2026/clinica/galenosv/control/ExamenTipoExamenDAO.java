package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenTipoExamen;

/**
 * Acceso a datos para la entidad {@link ExamenTipoExamen}.
 *
 * Hereda las operaciones CRUD proporcionadas por {@link DefaultDAO}.
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
}