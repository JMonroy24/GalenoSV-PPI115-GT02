package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.PersonaRol;

/**
 * Acceso a datos para la entidad .
 */
@ApplicationScoped
public class PersonaRolDAO extends DefaultDAO<PersonaRol, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

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
        return java.util.List.of("id");
    }


    /**
     * Busca registros para autocompletado (p:autoComplete), sin distinguir mayúsculas.
     *
     * @param filtro texto digitado por el usuario
     * @param max    cantidad máxima de resultados
     * @return lista de coincidencias
     */
    public List<PersonaRol> buscarParaAutocompletar(String filtro, int max) {
        String patron = "%" + (filtro == null ? "" : filtro.trim().toLowerCase()) + "%";
        return getEntityManager().createQuery(
                "SELECT pr FROM PersonaRol pr"
                + " LEFT JOIN pr.idPersona p"
                + " LEFT JOIN pr.idRol r"
                + " LEFT JOIN pr.idClinica c"
                + " WHERE LOWER(p.nombres) LIKE :patron"
                + " OR LOWER(p.apellidos) LIKE :patron"
                + " OR LOWER(r.nombre) LIKE :patron"
                + " OR LOWER(c.nombre) LIKE :patron",
                PersonaRol.class)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList();
    }
}
