package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

/** Error de dominio cuyo mensaje se puede presentar al usuario. */
public class ValidacionNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidacionNegocioException(String mensaje) {
        super(mensaje);
    }
}
