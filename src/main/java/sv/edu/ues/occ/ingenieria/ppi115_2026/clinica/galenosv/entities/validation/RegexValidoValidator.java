package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexValidoValidator implements ConstraintValidator<RegexValido, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return true;
        }
        try {
            Pattern.compile(valor);
            return true;
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }
}
