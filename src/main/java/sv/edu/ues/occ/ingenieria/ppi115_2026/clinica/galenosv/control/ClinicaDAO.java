package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Clinica;

/**
 * Acceso a datos para la entidad Clinica.
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

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("nombre", "tipo", "comentarios");
    }

    /** Catálogo activo, ordenado para los selectores de los formularios. */
    public java.util.List<Clinica> findAllActivos() {
        return getEntityManager().createQuery(
                "SELECT e FROM Clinica e WHERE e.activo = true ORDER BY e.nombre, e.idClinica",
                Clinica.class).getResultList();
    }
}
