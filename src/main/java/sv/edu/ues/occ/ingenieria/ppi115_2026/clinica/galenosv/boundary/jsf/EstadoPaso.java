package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.boundary.jsf;

/**
 * Estados del ciclo de vida de un {@code ConsultaProcedimientoPaso}.
 *
 * <p>Transiciones válidas:
 * <pre>
 *   PENDIENTE → EN_CURSO → COMPLETADO
 * </pre>
 * Cualquier otro salto es inválido y debe rechazarse en el Model.
 */
public enum EstadoPaso {

    /** Paso registrado pero aún no iniciado. Estado inicial obligatorio. */
    PENDIENTE,

    /** Paso en ejecución activa. Solo se puede llegar desde PENDIENTE. */
    EN_CURSO,

    /**
     * Paso finalizado. Solo se puede llegar desde EN_CURSO.
     * Una OrdenExamen solo puede crearse cuando el paso está EN_CURSO o COMPLETADO.
     */
    COMPLETADO;

    /**
     * Comprueba si la transición {@code origen → destino} es válida según
     * la máquina de estados definida.
     *
     * @param origen   estado actual del paso (puede ser null solo al crear)
     * @param destino  estado que se desea asignar
     * @return {@code true} si la transición está permitida
     */
    public static boolean transicionValida(EstadoPaso origen, EstadoPaso destino) {
        if (origen == null) {
            // Al crear un paso nuevo solo se puede arrancar en PENDIENTE
            return destino == PENDIENTE;
        }
        return switch (origen) {
            case PENDIENTE   -> destino == EN_CURSO;
            case EN_CURSO    -> destino == COMPLETADO;
            case COMPLETADO  -> false; // estado terminal
        };
    }

    /**
     * Convierte el {@code String} almacenado en la entidad JPA al enum.
     * Retorna {@code null} si el valor es null o vacío.
     *
     * @param valor cadena tal como se guarda en la columna {@code estado}
     * @return enum correspondiente, o {@code null}
     * @throws IllegalArgumentException si la cadena no coincide con ningún valor
     */
    public static EstadoPaso fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return EstadoPaso.valueOf(valor.trim().toUpperCase());
    }
}
