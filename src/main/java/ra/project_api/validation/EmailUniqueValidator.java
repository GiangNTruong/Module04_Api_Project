package ra.project_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import ra.project_api.repository.IUserRepository;

public class EmailUniqueValidator implements ConstraintValidator<EmailUnique,String> {
    @Autowired
    private IUserRepository userRepository;
    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return !userRepository.existsByEmail(email);
    }
}
