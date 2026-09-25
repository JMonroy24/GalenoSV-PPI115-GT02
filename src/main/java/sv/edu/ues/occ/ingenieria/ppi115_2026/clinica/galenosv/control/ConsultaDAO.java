package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta;

/**
 * Acceso a datos para la entidad Consulta.
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

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("referenciaExterna", "observaciones");
    }

    /**
     * Busca registros para autocompletado (p:autoComplete), sin distinguir mayúsculas.
     *
     * @param filtro texto digitado por el usuario
     * @param max    cantidad máxima de resultados
     * @return lista de coincidencias
     */
    public List<Consulta> buscarParaAutocompletar(String filtro, int max) {
        String patron = "%" + (filtro == null ? "" : filtro.trim().toLowerCase()) + "%";
        return getEntityManager().createQuery(
                "SELECT c FROM Consulta c"
                + " LEFT JOIN c.idPersonaRol pr"
                + " LEFT JOIN pr.idPersona p"
                + " WHERE LOWER(c.referenciaExterna) LIKE :patron"
                + " OR LOWER(p.nombres) LIKE :patron"
                + " OR LOWER(p.apellidos) LIKE :patron"
                + " ORDER BY c.fechaInicio DESC",
                Consulta.class)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList();
    }
}
