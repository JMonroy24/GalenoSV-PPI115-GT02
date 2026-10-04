package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.control;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Reglas compartidas independientes de JSF. */
public final class ValidadorComun {
    private ValidadorComun() { }

    public static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionNegocioException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    public static <T> T requerido(T valor, String mensaje) {
        if (valor == null) throw new ValidacionNegocioException(mensaje);
        return valor;
    }

    public static void activo(Boolean activo, String campo) {
        if (!Boolean.TRUE.equals(activo)) {
            throw new ValidacionNegocioException(campo + " está inactivo; seleccione una opción activa.");
        }
    }

    public static void rangoFechas(Date inicio, Date fin) {
        if (inicio != null && fin != null && fin.before(inicio)) {
            throw new ValidacionNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }

    public static void dentroDelPeriodo(Date inicio, Date fin, Date inicioPadre, Date finPadre) {
        if ((inicio != null && inicioPadre != null && inicio.before(inicioPadre))
                || (inicio != null && finPadre != null && inicio.after(finPadre))
                || (fin != null && finPadre != null && fin.after(finPadre))) {
            throw new ValidacionNegocioException("Las fechas deben estar dentro del período de la consulta.");
        }
    }

    public static void expresionRegular(String expresion) {
        if (expresion == null || expresion.isBlank()) return;
        try {
            Pattern.compile(expresion);
        } catch (PatternSyntaxException e) {
            throw new ValidacionNegocioException("La expresión regular no es válida.");
        }
    }

    public static void formato(String valor, String expresion, String indicaciones) {
        if (expresion == null || expresion.isBlank()) return;
        expresionRegular(expresion);
        if (valor == null || !Pattern.matches(expresion, valor)) {
            throw new ValidacionNegocioException("El valor no tiene el formato requerido."
                    + (indicaciones == null || indicaciones.isBlank() ? "" : " " + indicaciones.trim()));
        }
    }
}
