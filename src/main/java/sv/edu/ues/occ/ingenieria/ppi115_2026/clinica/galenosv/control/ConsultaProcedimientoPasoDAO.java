package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Acceso a datos para la entidad ConsultaProcedimientoPaso.
 */
@ApplicationScoped
public class ConsultaProcedimientoPasoDAO extends DefaultDAO<ConsultaProcedimientoPaso, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public ConsultaProcedimientoPasoDAO() {
        super(ConsultaProcedimientoPaso.class);
    }

    public ConsultaProcedimientoPasoDAO(EntityManager em) {
        super(ConsultaProcedimientoPaso.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected List<String> getCamposBusqueda() {
        return List.of("estado");
    }

    /**
     * Busca registros para autocompletado (p:autoComplete), sin distinguir mayúsculas.
     *
     * @param filtro texto digitado por el usuario
     * @param max    cantidad máxima de resultados
     * @return lista de coincidencias
     */
    public List<ConsultaProcedimientoPaso> buscarParaAutocompletar(String filtro, int max) {
        String texto = normalizarFiltro(filtro);
        if (texto == null || texto.length() < 2) {
            return java.util.List.of();
        }
        String patron = patronBusqueda(texto);
        return getEntityManager().createQuery(
                "SELECT cpp FROM ConsultaProcedimientoPaso cpp"
                + " LEFT JOIN FETCH cpp.idConsultaProcedimiento cp"
                + " LEFT JOIN FETCH cp.idProcedimiento proc"
                + " LEFT JOIN FETCH cpp.idPersonaRol pr"
                + " LEFT JOIN FETCH pr.idPersona p"
                + " WHERE LOWER(proc.nombre) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(cpp.estado) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.nombres) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.apellidos) LIKE :patron ESCAPE '\\'"
                + " ORDER BY cpp.fechaInicio DESC, cpp.idConsultaProcedimientoPaso",
                ConsultaProcedimientoPaso.class)
                .setParameter("patron", patron)
                .setMaxResults(limitarAutocompletado(max))
                .getResultList();
    }

    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idConsultaProcedimiento.idProcedimiento", "idPersonaRol.idPersona", "idPersonaRol.idRol");
    }
    
        public List<ConsultaProcedimientoPaso> findByConsultaProcedimiento(UUID idConsultaProcedimiento) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConsultaProcedimientoPaso> cq =
                cb.createQuery(ConsultaProcedimientoPaso.class);
        Root<ConsultaProcedimientoPaso> root = cq.from(ConsultaProcedimientoPaso.class);
        Fetch<ConsultaProcedimientoPaso, PersonaRol> pr =
                root.fetch("idPersonaRol", JoinType.LEFT);
        pr.fetch("idPersona", JoinType.LEFT);
        pr.fetch("idRol", JoinType.LEFT);
        cq.select(root)
                .where(cb.equal(root.get("idConsultaProcedimiento")
                        .get("idConsultaProcedimiento"), idConsultaProcedimiento))
                .orderBy(cb.asc(root.get("fechaInicio")));
        return em.createQuery(cq).getResultList();
    }
}
