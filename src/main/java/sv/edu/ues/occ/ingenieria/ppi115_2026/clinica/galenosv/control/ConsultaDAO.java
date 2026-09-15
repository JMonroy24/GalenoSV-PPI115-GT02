package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

/**
 * Acceso a datos para la entidad {@link Consulta}.
 */
@ApplicationScoped
public class ConsultaDAO extends DefaultDAO<Consulta, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ConsultaDAO() {
        super(Consulta.class);
    }

    public ConsultaDAO(EntityManager em) {
        super(Consulta.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
