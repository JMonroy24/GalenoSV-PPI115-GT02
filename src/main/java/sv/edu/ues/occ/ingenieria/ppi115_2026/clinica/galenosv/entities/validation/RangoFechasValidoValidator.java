package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Date;

public class RangoFechasValidoValidator implements ConstraintValidator<RangoFechasValido, PeriodoFechas> {

    @Override
    public boolean isValid(PeriodoFechas periodo, ConstraintValidatorContext context) {
        if (periodo == null || esValido(periodo.getFechaInicio(), periodo.getFechaFin())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("fechaFin").addConstraintViolation();
        return false;
    }

    /** Los campos obligatorios se validan por separado con NotNull. */
    public static boolean esValido(Date inicio, Date fin) {
        return inicio == null || fin == null || !fin.before(inicio);
    }
}
