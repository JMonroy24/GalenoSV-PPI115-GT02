package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import jakarta.persistence.EntityManager;
import java.util.List;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.MedioContacto;
import java.util.List;

/**
 * Acceso a datos para la entidad .
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

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("valor");
    }
}
