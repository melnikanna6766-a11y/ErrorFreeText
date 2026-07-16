package com.github.melnikanna6766a11y.errorfreetext.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LanguageValidator implements ConstraintValidator<ValidateLang, String> {
    @Override
    public void initialize(ValidateLang constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("ru") || value.equals("en");
    }
}
