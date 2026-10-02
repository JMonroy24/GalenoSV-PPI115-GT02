package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
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
        String texto = normalizarFiltro(filtro);
        if (texto == null || texto.length() < 2) {
            return java.util.List.of();
        }
        String patron = patronBusqueda(texto);
        return getEntityManager().createQuery(
                "SELECT cp FROM ConsultaProcedimiento cp"
                + " LEFT JOIN FETCH cp.idProcedimiento proc"
                + " LEFT JOIN FETCH cp.idConsulta c"
                + " LEFT JOIN FETCH c.idPersonaRol pr"
                + " LEFT JOIN FETCH pr.idPersona p"
                + " WHERE LOWER(proc.nombre) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.nombres) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.apellidos) LIKE :patron ESCAPE '\\'"
                + " ORDER BY cp.fechaInicio DESC, cp.idConsultaProcedimiento",
                ConsultaProcedimiento.class)
                .setParameter("patron", patron)
                .setMaxResults(limitarAutocompletado(max))
                .getResultList();
    }

    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idConsulta.idPersonaRol.idPersona", "idConsulta.idPersonaRol.idRol", "idProcedimiento");
    }
    
    public List<ConsultaProcedimiento> findByConsulta(UUID idConsulta) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConsultaProcedimiento> cq =
                cb.createQuery(ConsultaProcedimiento.class);
        Root<ConsultaProcedimiento> root = cq.from(ConsultaProcedimiento.class);
        root.fetch("idProcedimiento", JoinType.LEFT);
        cq.select(root)
                .where(cb.equal(root.get("idConsulta").get("idConsulta"), idConsulta))
                .orderBy(cb.asc(root.get("fechaInicio")));
        return em.createQuery(cq).getResultList();
    }
}
