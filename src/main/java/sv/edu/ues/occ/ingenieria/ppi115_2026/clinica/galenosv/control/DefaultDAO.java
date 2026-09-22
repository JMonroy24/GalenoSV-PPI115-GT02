package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Implementación abstracta base del patrón DAO genérico para entidades JPA.
 * Provee lógica CRUD reutilizable con JPA Criteria API.
 * Los métodos de escritura usan  (JTA) porque los DAOs
 * son beans CDI (@ApplicationScoped), no EJBs.
 *
 * @param <T>  tipo de la entidad JPA
 * @param <ID> tipo de la llave primaria; debe ser Serializable
 *
 */
public abstract class DefaultDAO<T, ID extends Serializable> implements DAOInterface<T, ID> {

    /** Clase de la entidad JPA, usada para find() y consultas Criteria. */
    private final Class<T> entityClass;

    /**
     * @param entityClass la clase de la entidad (por ejemplo, TipoExamen.class)
     */
    public DefaultDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Retorna el  inyectado por la subclase concreta.
     *
     * @return el  activo; nunca debe ser null
     */
    public abstract EntityManager getEntityManager();


    @Override
    @Transactional
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void update(T entity) {
        getEntityManager().merge(entity);
    }

    /**
     * {@inheritDoc}
     * Re-adjunta la entidad con merge() antes de invocar remove().
     */
    @Override
    @Transactional
    public void delete(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /** {@inheritDoc} */
    @Override
    public T findById(ID id) {
        return getEntityManager().find(entityClass, id);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findAll() {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findRange(int first, int max) {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public long count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(entityClass)));
        return getEntityManager().createQuery(cq).getSingleResult();
    }

    /**
     * Define los atributos JPA de tipo String para la búsqueda global.
     * Retorna lista vacía por defecto. Las subclases deben sobreescribirlo
     * para habilitar la búsqueda (ej. return List.of("nombres", "apellidos");).
     *
     * @return nombres de atributos JPA filtrables
     */
    protected List<String> getCamposBusqueda() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     * Aplica un filtro LIKE case-insensitive sobre los campos definidos en .
     */
    @Override
    public List<T> findRange(int first, int max, String filtroGlobal) {
        if (filtroGlobal == null || filtroGlobal.isBlank() || getCamposBusqueda().isEmpty()) {
            return findRange(first, max);
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);

        cq.where(construirPredicadoBusqueda(cb, root, filtroGlobal));

        return getEntityManager().createQuery(cq)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Cuenta solo los registros que coinciden con el filtro global.
     */
    @Override
    public long count(String filtroGlobal) {
        if (filtroGlobal == null || filtroGlobal.isBlank() || getCamposBusqueda().isEmpty()) {
            return count();
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));

        cq.where(construirPredicadoBusqueda(cb, root, filtroGlobal));

        return getEntityManager().createQuery(cq).getSingleResult();
    }

    /**
     * Construye un predicado OR que hace LOWER(campo) LIKE %filtro%
     * sobre cada campo retornado por .
     */
    private Predicate construirPredicadoBusqueda(CriteriaBuilder cb, Root<T> root, String filtroGlobal) {
        String patron = "%" + filtroGlobal.toLowerCase() + "%";
        Predicate[] predicados = getCamposBusqueda().stream()
                .map(campo -> cb.like(cb.lower(root.get(campo).as(String.class)), patron))
                .toArray(Predicate[]::new);
        return cb.or(predicados);
    }
}
