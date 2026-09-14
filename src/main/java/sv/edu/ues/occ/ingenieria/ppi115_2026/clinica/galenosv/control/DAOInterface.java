package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.io.Serializable;
import java.util.List;

/**
 * Contrato genérico para operaciones CRUD sobre cualquier entidad JPA.
 *
 * @param <T>  tipo de la entidad JPA
 * @param <ID> tipo de la llave primaria; debe ser {@link Serializable}
 *
 */
public interface DAOInterface<T, ID extends Serializable> {

    /**
     * Persiste una nueva entidad en la base de datos.
     * @param entity la entidad a persistir; no debe ser {@code null}
     */
    void create(T entity);

    /**
     * Actualiza una entidad existente en la base de datos.
     * @param entity la entidad con los datos actualizados; no debe ser {@code null}
     */
    void update(T entity);

    /**
     * Elimina una entidad de la base de datos.
     * @param entity la entidad a eliminar; no debe ser {@code null}
     */
    void delete(T entity);

    /**
     * Busca una entidad por su llave primaria.
     *
     * @param id la llave primaria; no debe ser {@code null}
     * @return la entidad encontrada, o {@code null} si no existe
     */
    T findById(ID id);

    /**
     * Retorna todos los registros de la entidad.
     *
     * @return lista con todas las entidades; nunca es {@code null}
     */
    List<T> findAll();

    /**
     * Retorna un subconjunto paginado de registros.
     *
     * @param first índice del primer resultado (base 0)
     * @param max   cantidad máxima de resultados
     * @return lista con los registros del rango solicitado
     */
    List<T> findRange(int first, int max);

    /**
     * Retorna la cantidad total de registros en la base de datos.
     *
     * @return número total de registros
     */
    long count();
}
