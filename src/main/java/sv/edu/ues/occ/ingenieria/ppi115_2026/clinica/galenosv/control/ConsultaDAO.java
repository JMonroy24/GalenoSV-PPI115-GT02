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
        String texto = normalizarFiltro(filtro);
        if (texto == null || texto.length() < 2) {
            return java.util.List.of();
        }
        String patron = patronBusqueda(texto);
        return getEntityManager().createQuery(
                "SELECT c FROM Consulta c"
                + " LEFT JOIN FETCH c.idPersonaRol pr"
                + " LEFT JOIN FETCH pr.idPersona p"
                + " WHERE LOWER(c.referenciaExterna) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.nombres) LIKE :patron ESCAPE '\\'"
                + " OR LOWER(p.apellidos) LIKE :patron ESCAPE '\\'"
                + " ORDER BY c.fechaInicio DESC, c.idConsulta",
                Consulta.class)
                .setParameter("patron", patron)
                .setMaxResults(limitarAutocompletado(max))
                .getResultList();
    }

    @Override
    protected java.util.List<String> getRelacionesCarga() {
        return java.util.List.of("idPersonaRol.idPersona", "idPersonaRol.idRol", "idPersonaRol.idClinica");
    }

    public List<Consulta> findRangeFiltrado(int first, int max, UUID idClinica,
            java.util.Date desde, java.util.Date hasta) {
        return findRangeFiltrado(first, max, idClinica, desde, hasta, null);
    }

    public List<Consulta> findRangeFiltrado(int first, int max, UUID idClinica,
            java.util.Date desde, java.util.Date hasta, String filtro) {
        if (idClinica == null) return List.of();
        if (first < 0 || max <= 0) throw new IllegalArgumentException("Paginación inválida.");
        return consultaFiltrada(false, Consulta.class, idClinica, desde, hasta, filtro)
                .setFirstResult(first).setMaxResults(max).getResultList();
    }

    public long countFiltrado(UUID idClinica, java.util.Date desde, java.util.Date hasta) {
        return countFiltrado(idClinica, desde, hasta, null);
    }

    public long countFiltrado(UUID idClinica, java.util.Date desde, java.util.Date hasta, String filtro) {
        if (idClinica == null) return 0;
        return consultaFiltrada(true, Long.class, idClinica, desde, hasta, filtro).getSingleResult();
    }

    /** No enlaza parámetros nulos; el listado y el conteo comparten condiciones. */
    private <T> jakarta.persistence.TypedQuery<T> consultaFiltrada(boolean contar, Class<T> tipo,
            UUID idClinica, java.util.Date desde, java.util.Date hasta, String filtro) {
        String texto = normalizarFiltro(filtro);
        String jpql = (contar ? "SELECT COUNT(c) FROM Consulta c JOIN c.idPersonaRol pr"
                : "SELECT c FROM Consulta c JOIN FETCH c.idPersonaRol pr"
                    + " JOIN FETCH pr.idPersona JOIN FETCH pr.idRol JOIN FETCH pr.idClinica")
                + " WHERE pr.idClinica.idClinica = :clinica"
                + (desde == null ? "" : " AND c.fechaInicio >= :desde")
                + (hasta == null ? "" : " AND c.fechaInicio <= :hasta")
                + (texto == null ? "" : " AND (LOWER(c.referenciaExterna) LIKE :patron ESCAPE '\\'"
                    + " OR LOWER(c.observaciones) LIKE :patron ESCAPE '\\')")
                + (contar ? "" : " ORDER BY c.fechaInicio DESC, c.idConsulta");
        var query = getEntityManager().createQuery(jpql, tipo).setParameter("clinica", idClinica);
        if (desde != null) query.setParameter("desde", desde);
        if (hasta != null) query.setParameter("hasta", hasta);
        if (texto != null) query.setParameter("patron", patronBusqueda(texto));
        return query;
    }

}
