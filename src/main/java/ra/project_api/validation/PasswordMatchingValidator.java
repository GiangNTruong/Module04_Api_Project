package ra.project_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordMatchingValidator implements ConstraintValidator<PasswordMatching,Object> {
    private String passwordField;
    private String confirmPasswordField;

    @Override
    public boolean isValid(Object changePassword, ConstraintValidatorContext context) {
       Object passwordValue = new BeanWrapperImpl(changePassword).getPropertyValue(passwordField);
       Object confirmPasswordValue = new BeanWrapperImpl(changePassword).getPropertyValue(confirmPasswordField);
       if (passwordValue==null || !passwordValue.equals(confirmPasswordValue)){
           context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode(confirmPasswordField)
                   .addConstraintViolation();
           return false;
       }
       return true;
    }

    @Override
    public void initialize(PasswordMatching passwordMatching) {
        passwordField = passwordMatching.password();
        confirmPasswordField = passwordMatching.confirmPassword();
    }
}
