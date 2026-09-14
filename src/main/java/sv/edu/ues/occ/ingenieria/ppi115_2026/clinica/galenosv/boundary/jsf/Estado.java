package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

/**
 * Estados posibles de una operación CRUD en un backing bean JSF.
 * Controla qué acción ejecuta {@link Model#guardar()} y qué muestra la vista.
 *
 */
public enum Estado {

    /** Sin operación activa; modo de solo lectura/listado. */
    NINGUNO,

    /** Creando un registro nuevo. */
    CREAR,

    /** Editando un registro existente. */
    MODIFICAR,

    /** Procesando la eliminación de un registro. */
    ELIMINAR
}
