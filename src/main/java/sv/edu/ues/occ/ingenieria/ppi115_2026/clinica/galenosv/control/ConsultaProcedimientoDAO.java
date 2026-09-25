package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento;

/**
 * Acceso a datos para la entidad ConsultaProcedimiento.
 */
@ApplicationScoped
public class ConsultaProcedimientoDAO extends DefaultDAO<ConsultaProcedimiento, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ConsultaProcedimientoDAO() {
        super(ConsultaProcedimiento.class);
    }

    public ConsultaProcedimientoDAO(EntityManager em) {
        super(ConsultaProcedimiento.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("observaciones");
    }

    /**
     * Busca registros para autocompletado (p:autoComplete), sin distinguir mayúsculas.
     *
     * @param filtro texto digitado por el usuario
     * @param max    cantidad máxima de resultados
     * @return lista de coincidencias
     */
    public List<ConsultaProcedimiento> buscarParaAutocompletar(String filtro, int max) {
        String patron = "%" + (filtro == null ? "" : filtro.trim().toLowerCase()) + "%";
        return getEntityManager().createQuery(
                "SELECT cp FROM ConsultaProcedimiento cp"
                + " LEFT JOIN cp.idProcedimiento proc"
                + " LEFT JOIN cp.idConsulta c"
                + " LEFT JOIN c.idPersonaRol pr"
                + " LEFT JOIN pr.idPersona p"
                + " WHERE LOWER(proc.nombre) LIKE :patron"
                + " OR LOWER(p.nombres) LIKE :patron"
                + " OR LOWER(p.apellidos) LIKE :patron"
                + " ORDER BY cp.fechaInicio DESC",
                ConsultaProcedimiento.class)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList();
    }
}
