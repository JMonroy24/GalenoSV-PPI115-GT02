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
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Acceso a datos para la entidad PersonaRol.
 */
@ApplicationScoped
public class PersonaRolDAO extends DefaultDAO<PersonaRol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String ROL_PACIENTE = "paciente";


    @PersistenceContext(unitName = "GalenoSV_PU")
    protected EntityManager em;

    public PersonaRolDAO() {
        super(PersonaRol.class);
    }

    public PersonaRolDAO(EntityManager em) {
        super(PersonaRol.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected java.util.List<String> getCamposBusqueda() {
        return java.util.List.of("idPersona.nombres", "idPersona.apellidos", "idRol.nombre", "idClinica.nombre");
    }


    /**
     * Busca registros para autocompletado (p:autoComplete), sin distinguir mayúsculas.
     *
     * @param filtro texto digitado por el usuario
     * @param max    cantidad máxima de resultados
     * @return lista de coincidencias
     */
    public List<PersonaRol> buscarParaAutocompletar(String filtro, int max) {
        String texto = normalizarFiltro(filtro);
        if (texto == null || texto.length() < 2) {
            return java.util.List.of();
        }
        String patron = patronBusqueda(texto);
        return getEntityManager().createQuery(
                "SELECT pr FROM PersonaRol pr"
                + " LEFT JOIN FETCH pr.idPersona p"
                + " LEFT JOIN FETCH pr.idRol r"
                + " LEFT JOIN FETCH pr.idClinica c"
                + " WHERE LOWER(p.nombres) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.apellidos) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(r.nombre) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(c.nombre) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(CONCAT(p.nombres, ' ', p.apellidos, ' ', r.nombre)) LIKE :patron ESCAPE '\\'"
                + " ORDER BY p.apellidos, p.nombres, pr.idPersonaRol",
                PersonaRol.class)
                .setParameter("patron", patron)
                .setMaxResults(limitarAutocompletado(max))
                .getResultList();
    }

    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idPersona", "idRol", "idClinica");
    }


    public List<PersonaRol> findByClinicaAndRol(UUID idClinica, UUID idRol) {
        if (idClinica == null || idRol == null) return List.of();
        return getEntityManager().createQuery(
                "SELECT pr FROM PersonaRol pr JOIN FETCH pr.idPersona p JOIN FETCH pr.idRol r JOIN FETCH pr.idClinica c "
                + "WHERE c.idClinica = :clinica AND r.idRol = :rol AND c.activo = true AND r.activo = true "
                + "ORDER BY p.apellidos, p.nombres", PersonaRol.class)
                .setParameter("clinica", idClinica)
                .setParameter("rol", idRol)
                .getResultList();
    }

    public boolean existeAsignacion(UUID idPersona, UUID idRol, UUID idClinica, UUID excluirId) {
        if (idPersona == null || idRol == null) return false;
        String jpql = "SELECT COUNT(p) FROM PersonaRol p"
                + " WHERE p.idPersona.idPersona = :persona AND p.idRol.idRol = :rol"
                + (idClinica == null ? " AND p.idClinica IS NULL" : " AND p.idClinica.idClinica = :clinica")
                + (excluirId == null ? "" : " AND p.idPersonaRol <> :excluir");
        var query = getEntityManager().createQuery(jpql, Long.class)
                .setParameter("persona", idPersona).setParameter("rol", idRol);
        if (idClinica != null) query.setParameter("clinica", idClinica);
        if (excluirId != null) query.setParameter("excluir", excluirId);
        return query.getSingleResult() > 0;
    }


    public List<PersonaRol> findByPersona(UUID idPersona) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PersonaRol> cq = cb.createQuery(PersonaRol.class);
        Root<PersonaRol> root = cq.from(PersonaRol.class);
        root.fetch("idRol", JoinType.LEFT);
        root.fetch("idClinica", JoinType.LEFT);
        cq.select(root).where(
                cb.equal(root.get("idPersona").get("idPersona"), idPersona));
        return em.createQuery(cq).getResultList();
    }

    /** Pacientes con rol activo de la clínica indicada; búsqueda literal acotada. */
    public List<PersonaRol> buscarPacientes(String filtro, UUID idClinica, int max) {
        String texto = normalizarFiltro(filtro);
        if (idClinica == null || texto == null || texto.length() < 2) return List.of();
        return getEntityManager().createQuery(
                "SELECT pr FROM PersonaRol pr JOIN FETCH pr.idPersona p"
                + " JOIN FETCH pr.idRol r JOIN FETCH pr.idClinica c"
                + " WHERE c.idClinica = :clinica AND c.activo = true AND r.activo = true"
                + " AND LOWER(r.nombre) LIKE :rolPaciente"
                + " AND (LOWER(p.nombres) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.apellidos) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE :patron ESCAPE '\\')"
                + " ORDER BY p.apellidos, p.nombres, pr.idPersonaRol", PersonaRol.class)
                .setParameter("clinica", idClinica).setParameter("rolPaciente", "%" + ROL_PACIENTE + "%").setParameter("patron", patronBusqueda(texto))
                .setMaxResults(limitarAutocompletado(max)).getResultList();
    }

    /** Responsable del rol requerido en la clínica, elegido de forma estable. */
    public PersonaRol findResponsable(UUID idClinica, UUID idRol) {
        if (idClinica == null || idRol == null) return null;
        return getEntityManager().createQuery(
                "SELECT pr FROM PersonaRol pr JOIN FETCH pr.idPersona JOIN FETCH pr.idRol r"
                + " JOIN FETCH pr.idClinica c WHERE c.idClinica = :clinica AND c.activo = true"
                + " AND r.idRol = :rol AND r.activo = true ORDER BY pr.idPersonaRol", PersonaRol.class)
                .setParameter("clinica", idClinica).setParameter("rol", idRol).setMaxResults(1)
                .getResultList().stream().findFirst().orElse(null);
    }

}
