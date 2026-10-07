package sv.edu.ues.occ.ingenieria.ppi115_2026.clinica.galenosv.entities.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegexValidoValidator.class)
public @interface RegexValido {

    String message() default "{regex.valida}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
