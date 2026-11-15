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
    @Override
    public List<Consulta> buscarParaAutocompletar(String filtro, int max) {
        return autocompletarConConsulta("Consulta.buscarParaAutocompletar", filtro, max, 2);
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

    /** Parámetros opcionales con valor tipado; nunca se enlazan UUID ni fechas nulos. */
    private <T> jakarta.persistence.TypedQuery<T> consultaFiltrada(boolean contar, Class<T> tipo,
            UUID idClinica, java.util.Date desde, java.util.Date hasta, String filtro) {
        String texto = normalizarFiltro(filtro);
        return getEntityManager().createNamedQuery(contar ? "Consulta.countFiltrado" : "Consulta.findRangeFiltrado", tipo)
                .setParameter("clinica", idClinica)
                .setParameter("filtrarDesde", desde != null)
                .setParameter("desde", desde == null ? new java.util.Date(0) : desde)
                .setParameter("filtrarHasta", hasta != null)
                .setParameter("hasta", hasta == null ? new java.util.Date(0) : hasta)
                .setParameter("filtrarTexto", texto != null)
                .setParameter("patron", texto == null ? "%" : patronBusqueda(texto));
    }

    /** Sugerencias limitadas a pacientes con rol y clínica activos de la sesión. */
    public List<Consulta> buscarParaAutocompletar(String filtro, int max, UUID idClinica) {
        String texto = normalizarFiltro(filtro);
        if (texto == null || texto.length() < 2 || idClinica == null) return List.of();
        return getEntityManager().createNamedQuery("Consulta.autocompletarPorClinica", Consulta.class)
                .setParameter("clinica", idClinica).setParameter("rolPaciente", "%paciente%")
                .setParameter("patron", patronBusqueda(texto))
                .setMaxResults(limitarAutocompletado(max)).getResultList();
    }
}
