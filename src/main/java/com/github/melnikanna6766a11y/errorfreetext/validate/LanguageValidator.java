package com.github.melnikanna6766a11y.errorfreetext.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LanguageValidator implements ConstraintValidator<ValidateLang, String> {
    @Override
    public void initialize(ValidateLang constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.info("validate language {}", value);
        return value.equals("ru") || value.equals("en");
    }
}
