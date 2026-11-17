package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.Persona;

@ApplicationScoped
public class PersonaService extends DefaultService<Persona, UUID> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected PersonaDAO personaDAO;

    public PersonaService() {
    }

    public PersonaService(PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }

    @Override
    protected DefaultDAO<Persona, UUID> getDAO() {
        return personaDAO;
    }

    public List<Persona> findRange(int first, int max, String filtro,
            UUID tipoDocumentoId, String valor) {
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("El inicio debe ser mayor o igual a cero y el tamaño debe ser positivo.");
        }
        if (tipoDocumentoId == null && valor == null) {
            return super.findRange(first, max, filtro);
        }
        var em = personaDAO.getEntityManager();
        var cb = em.getCriteriaBuilder();
        var consulta = cb.createQuery(Persona.class);
        var persona = consulta.from(Persona.class);
        consulta.select(persona).distinct(true);
        consulta.where(filtrosDocumento(cb, persona, filtro, tipoDocumentoId, valor));
        consulta.orderBy(cb.asc(persona.get("idPersona")));
        return em.createQuery(consulta)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    public long count(String filtro, UUID tipoDocumentoId, String valor) {
        if (tipoDocumentoId == null && valor == null) {
            return super.count(filtro);
        }
        var em = personaDAO.getEntityManager();
        var cb = em.getCriteriaBuilder();
        var consulta = cb.createQuery(Long.class);
        var persona = consulta.from(Persona.class);
        consulta.select(cb.countDistinct(persona));
        consulta.where(filtrosDocumento(cb, persona, filtro, tipoDocumentoId, valor));
        return em.createQuery(consulta).getSingleResult();
    }

    private Predicate[] filtrosDocumento(CriteriaBuilder cb, Root<Persona> persona,
            String filtro, UUID tipoDocumentoId, String valor) {
        List<Predicate> predicados = new ArrayList<>();
        // Ambos filtros deben coincidir en el mismo documento de la persona.
        Join<Persona, Documento> documento = persona.join("documentoList");
        if (valor != null) {
            predicados.add(cb.equal(documento.get("valor"), valor));
        }
        if (tipoDocumentoId != null) {
            predicados.add(cb.equal(documento.get("idTipoDocumento").get("idTipoDocumento"), tipoDocumentoId));
        }
        if (normalizarFiltro(filtro) != null) {
            String patron = DefaultDAO.patronBusqueda(filtro);
            predicados.add(cb.or(
                    cb.like(cb.lower(persona.get("nombres")), patron, '\\'),
                    cb.like(cb.lower(persona.get("apellidos")), patron, '\\')));
        }
        return predicados.toArray(Predicate[]::new);
    }

    @Override
    protected void validar(Persona r, boolean nuevo) {
        if (nuevo && r.getFechaCreacion() == null) {
            r.setFechaCreacion(new Date());
        }
        r.setNombres(ValidadorComun.textoObligatorio(r.getNombres(), "El campo Nombres"));
        r.setApellidos(ValidadorComun.textoObligatorio(r.getApellidos(), "El campo Apellidos"));
        if (r.getFechaNacimiento() != null && !r.getFechaNacimiento().before(getHoy())) {
            throw new ValidacionNegocioException("La fecha de nacimiento debe estar en el pasado.");
        }
    }

    public Date getHoy() {
        var zona = java.time.ZoneId.of("America/El_Salvador");
        return Date.from(java.time.LocalDate.now(zona).atStartOfDay(zona).toInstant());
    }

    public PersonaDAO getPersonaDAO() {
        return personaDAO;
    }

    public void setPersonaDAO(PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }
}
