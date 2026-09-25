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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ExamenResultado;
import java.util.List;

/**
 * Acceso a datos para la entidad .
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

    public java.util.List<ExamenResultado> buscarParaAutocompletar(String filtro, int max) {
        String patron = "%" + (filtro == null ? "" : filtro.trim().toLowerCase()) + "%";
        return getEntityManager().createQuery(
                "SELECT e FROM ExamenResultado e",
                ExamenResultado.class)
                .setMaxResults(max)
                .getResultList();
    }
}
