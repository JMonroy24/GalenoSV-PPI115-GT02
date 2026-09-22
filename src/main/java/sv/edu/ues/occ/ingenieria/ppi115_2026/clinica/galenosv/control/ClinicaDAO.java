package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

/**
 * Acceso a datos para la entidad .
 */
@ApplicationScoped
public class ClinicaDAO extends DefaultDAO<Clinica, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ClinicaDAO() {
        super(Clinica.class);
    }

    public ClinicaDAO(EntityManager em) {
        super(Clinica.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
