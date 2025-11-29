package com.myproject.nexa.validation;

import com.myproject.nexa.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.passay.*;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;

/**
 * Custom password validator with strong password policy
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
@Documented
public @interface StrongPassword {

    String message() default "Password does not meet security requirements";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

/**
 * Validator implementation for password strength
 */
@RequiredArgsConstructor
class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private final AppProperties appProperties;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        // Initialization code if needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Use Passay library for comprehensive password validation
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
            // Length rule: at least 12 characters
            new LengthRule(12, 128),
            // At least one uppercase character
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            // At least one lowercase character
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            // At least one digit
            new CharacterRule(EnglishCharacterData.Digit, 1),
            // At least one special character
            new CharacterRule(EnglishCharacterData.Special, 1),
            // No whitespace
            new WhitespaceRule()
        ));

        RuleResult result = validator.validate(new PasswordData(password));
        
        if (result.isValid()) {
            return true;
        }

        // Set custom error messages
        List<String> messages = validator.getMessages(result);
        String message = String.join(", ", messages);
        
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        
        return false;
    }
}