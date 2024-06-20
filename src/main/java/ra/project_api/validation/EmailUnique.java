package ra.project_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailUniqueValidator.class)
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailUnique {
    //Khai báo các thuộc tiính của annotion
    String message() default "{Email is exists}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
