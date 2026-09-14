package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

/**
 * Implementación abstracta base del patrón DAO genérico para entidades JPA.
 * Provee lógica CRUD reutilizable con JPA Criteria API.
 * Los métodos de escritura usan {@link Transactional} (JTA) porque los DAOs
 * son beans CDI ({@code @ApplicationScoped}), no EJBs.
 *
 * @param <T>  tipo de la entidad JPA
 * @param <ID> tipo de la llave primaria; debe ser {@link Serializable}
 *
 */
public abstract class DefaultDAO<T, ID extends Serializable> implements DAOInterface<T, ID> {

    /** Clase de la entidad JPA, usada para {@code find()} y consultas Criteria. */
    private final Class<T> entityClass;

    /**
     * @param entityClass la clase de la entidad (por ejemplo, {@code TipoExamen.class})
     */
    public DefaultDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Retorna el {@link EntityManager} inyectado por la subclase concreta.
     *
     * @return el {@link EntityManager} activo; nunca debe ser {@code null}
     */
    public abstract EntityManager getEntityManager();

    /** {@inheritDoc} */
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
     * Re-adjunta la entidad con {@code merge()} antes de invocar {@code remove()}.
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
}
