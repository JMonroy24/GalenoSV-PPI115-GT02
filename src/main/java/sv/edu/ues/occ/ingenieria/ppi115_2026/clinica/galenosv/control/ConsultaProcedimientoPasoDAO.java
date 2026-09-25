package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.ConsultaProcedimientoPaso;

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
        String patron = "%" + (filtro == null ? "" : filtro.trim().toLowerCase()) + "%";
        return getEntityManager().createQuery(
                "SELECT cpp FROM ConsultaProcedimientoPaso cpp"
                + " LEFT JOIN cpp.idConsultaProcedimiento cp"
                + " LEFT JOIN cp.idProcedimiento proc"
                + " LEFT JOIN cpp.idPersonaRol pr"
                + " LEFT JOIN pr.idPersona p"
                + " WHERE LOWER(proc.nombre) LIKE :patron"
                + " OR LOWER(cpp.estado) LIKE :patron"
                + " OR LOWER(p.nombres) LIKE :patron"
                + " OR LOWER(p.apellidos) LIKE :patron"
                + " ORDER BY cpp.fechaInicio DESC",
                ConsultaProcedimientoPaso.class)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList();
    }
}
