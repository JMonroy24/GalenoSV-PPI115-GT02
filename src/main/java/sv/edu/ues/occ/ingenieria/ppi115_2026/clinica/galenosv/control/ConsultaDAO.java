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
    public List<Consulta> findRangeRest(int first, int max, FiltroConsultasRest filtro) {
        if (first < 0 || max < 1) throw new IllegalArgumentException("Paginación inválida.");
        return consultaRest(false, Consulta.class, filtro).setFirstResult(first).setMaxResults(max).getResultList();
    }

    public long countRest(FiltroConsultasRest filtro) {
        return consultaRest(true, Long.class, filtro).getSingleResult();
    }

    /** EXISTS conserva una fila por consulta, incluso con varios procedimientos y pasos. */
    private <T> jakarta.persistence.TypedQuery<T> consultaRest(boolean contar, Class<T> tipo, FiltroConsultasRest f) {
        StringBuilder jpql = new StringBuilder(contar ? "SELECT COUNT(c) FROM Consulta c WHERE 1=1"
                : "SELECT c FROM Consulta c WHERE 1=1");
        java.util.Map<String, Object> parametros = new java.util.LinkedHashMap<>();
        if (f.pacienteId() != null) {
            jpql.append(" AND c.idPersonaRol.idPersonaRol = :paciente"); parametros.put("paciente", f.pacienteId());
        }
        if (f.idClinica() != null) {
            jpql.append(" AND c.idPersonaRol.idClinica.idClinica = :clinica"); parametros.put("clinica", f.idClinica());
        }
        if (f.desde() != null) { jpql.append(" AND c.fechaInicio >= :desde"); parametros.put("desde", f.desde()); }
        if (f.hasta() != null) { jpql.append(" AND c.fechaInicio <= :hasta"); parametros.put("hasta", f.hasta()); }
        if (normalizarFiltro(f.q()) != null) {
            jpql.append(" AND (LOWER(c.referenciaExterna) LIKE :patron ESCAPE '\\'"
                    + " OR LOWER(c.observaciones) LIKE :patron ESCAPE '\\')");
            parametros.put("patron", patronBusqueda(f.q()));
        }
        if (f.idRol() != null) {
            jpql.append(" AND EXISTS (SELECT p FROM ConsultaProcedimientoPaso p"
                    + " WHERE p.idConsultaProcedimiento.idConsulta = c"
                    + " AND p.idProcedimientoPaso.idRol.idRol = :rol"
                    + " AND p.idProcedimientoPaso.idRol.activo = true"
                    + " AND (p.estado = 'EN_CURSO'"
                    + " OR (p.estado = 'PENDIENTE' AND NOT EXISTS (SELECT a FROM ConsultaProcedimientoPaso a"
                    + " WHERE a.idConsultaProcedimiento = p.idConsultaProcedimiento AND a.estado = 'EN_CURSO'))"
                    + " OR (p.estado = 'COMPLETADO' AND NOT EXISTS (SELECT a FROM ConsultaProcedimientoPaso a"
                    + " WHERE a.idConsultaProcedimiento = p.idConsultaProcedimiento"
                    + " AND (a.estado IN ('PENDIENTE', 'EN_CURSO') OR a.fechaInicio > p.fechaInicio)))))");
            parametros.put("rol", f.idRol());
        }
        if (!contar) jpql.append(" ORDER BY c.fechaInicio DESC, c.idConsulta");
        var query = em.createQuery(jpql.toString(), tipo);
        parametros.forEach(query::setParameter);
        return query;
    }

}
