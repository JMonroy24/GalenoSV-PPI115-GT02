package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;

/**
 * Acceso a datos para la entidad {@link ExamenResultado}.
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
}
