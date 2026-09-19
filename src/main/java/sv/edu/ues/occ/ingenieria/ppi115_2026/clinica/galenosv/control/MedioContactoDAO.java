package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;

/**
 * Acceso a datos para la entidad {@link MedioContacto}.
 */
@ApplicationScoped
public class MedioContactoDAO extends DefaultDAO<MedioContacto, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public MedioContactoDAO() {
        super(MedioContacto.class);
    }

    public MedioContactoDAO(EntityManager em) {
        super(MedioContacto.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
