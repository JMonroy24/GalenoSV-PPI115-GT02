package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.rest.error.PreconditionFailedException;

/**
 * Base genérica para servicios de la capa de control.
 * Proporciona el límite transaccional, hooks de validación de negocio y
 * delega la persistencia y consultas al DAO correspondiente.
 *
 * Implementa {@link DAOInterface} para mantener compatibilidad directa con
 * {@link sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf.GenericLazyDataModel}.
 *
 * @param <T>  tipo de la entidad JPA
 * @param <ID> tipo del identificador; debe ser Serializable
 */
public abstract class DefaultService<T, ID extends Serializable> implements DAOInterface<T, ID> {

    /** Retorna el DAO concreto asociado a la entidad. */
    protected abstract DefaultDAO<T, ID> getDAO();

    /** Hook de validación de reglas de negocio antes de persistir. */
    protected void validar(T entidad, boolean nuevo) {
    }

    @Transactional
    public void crear(T e) {
        validar(e, true);
        getDAO().create(e);
    }

    @Transactional
    public void actualizar(T e) {
        validar(e, false);
        getDAO().update(e);
    }

    @Transactional
    public T actualizarSi(ID id, Predicate<T> precondicion, Consumer<T> cambios) {
        T entidad = findById(id);
        if (entidad == null) {
            throw new NotFoundException("No existe el recurso solicitado.");
        }
        if (!precondicion.test(entidad)) {
            throw new PreconditionFailedException(
                    "El ETag enviado no coincide con el estado actual del recurso.");
        }
        cambios.accept(entidad);
        validar(entidad, false);
        getDAO().update(entidad);
        return entidad;
    }

    @Transactional
    public void eliminar(T e) {
        getDAO().delete(e);
    }

    @Override
    @Transactional
    public void create(T entity) {
        crear(entity);
    }

    @Override
    @Transactional
    public void update(T entity) {
        actualizar(entity);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        eliminar(entity);
    }

    @Override
    public T findById(ID id) {
        return getDAO().findById(id);
    }

    @Override
    public List<T> findAll() {
        return getDAO().findAll();
    }

    @Override
    public List<T> findRange(int first, int max) {
        return getDAO().findRange(first, max);
    }

    @Override
    public long count() {
        return getDAO().count();
    }

    @Override
    public List<T> findRange(int first, int max, String filtroGlobal) {
        return getDAO().findRange(first, max, filtroGlobal);
    }

    @Override
    public long count(String filtroGlobal) {
        return getDAO().count(filtroGlobal);
    }

    @Override
    public List<T> findRange(int first, int max, String filtroGlobal,
            List<OrdenConsulta> orden, List<FiltroConsulta> filtros) {
        return getDAO().findRange(first, max, filtroGlobal, orden, filtros);
    }

    @Override
    public long count(String filtroGlobal, List<FiltroConsulta> filtros) {
        return getDAO().count(filtroGlobal, filtros);
    }

    public List<T> buscarParaAutocompletar(String filtro, int max) {
        return getDAO().buscarParaAutocompletar(filtro, max);
    }

    public static String normalizarFiltro(String valor) {
        return DefaultDAO.normalizarFiltro(valor);
    }
}
