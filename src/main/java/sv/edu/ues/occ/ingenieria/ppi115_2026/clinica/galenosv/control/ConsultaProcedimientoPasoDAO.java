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
    @Override
    public List<ConsultaProcedimientoPaso> buscarParaAutocompletar(String filtro, int max) {
        return autocompletarConConsulta("ConsultaProcedimientoPaso.buscarParaAutocompletar", filtro, max, 2);
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
    /** Bloquea primero el padre y refresca el paso para comparar el ETag con datos actuales. */
    public ConsultaProcedimientoPaso findByIdParaFlujo(UUID id) {
        var paso = em.find(ConsultaProcedimientoPaso.class, id);
        if (paso == null) return null;
        bloquearConsulta(paso);
        em.refresh(paso, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        return paso;
    }

    public void bloquearConsulta(ConsultaProcedimientoPaso paso) {
        var referencia = ValidadorComun.requerido(paso.getIdConsultaProcedimiento(), "Seleccione un procedimiento de consulta.");
        // Los mappers REST resuelven relaciones antes de entrar en la transacción: pueden estar detached.
        var asignado = ValidadorComun.requerido(em.find(
                sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimiento.class,
                referencia.getIdConsultaProcedimiento()), "El procedimiento de consulta ya no existe.");
        var consulta = ValidadorComun.requerido(asignado.getIdConsulta(), "El procedimiento debe pertenecer a una consulta.");
        consulta = em.find(sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Consulta.class,
                consulta.getIdConsulta(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        em.refresh(consulta, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        em.refresh(asignado);
        paso.setIdConsultaProcedimiento(asignado);
    }

    public ConsultaProcedimientoPaso findRegistro(UUID asignado, UUID definicion) {
        var resultados = em.createQuery("SELECT p FROM ConsultaProcedimientoPaso p"
                + " WHERE p.idConsultaProcedimiento.idConsultaProcedimiento = :asignado"
                + " AND p.idProcedimientoPaso.idProcedimientoPaso = :definicion"
                + " ORDER BY CASE WHEN p.estado = 'EN_CURSO' THEN 0 WHEN p.estado = 'PENDIENTE' THEN 1 ELSE 2 END,"
                + " p.fechaInicio DESC, p.idConsultaProcedimientoPaso", ConsultaProcedimientoPaso.class)
                .setParameter("asignado", asignado).setParameter("definicion", definicion)
                .setHint("eclipselink.refresh", true).getResultList();
        return resultados.isEmpty() ? null : resultados.getFirst();
    }

}
