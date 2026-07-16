package com.github.melnikanna6766a11y.errorfreetext.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequestTextValidator implements ConstraintValidator<ValidateRequestText, String> {
    @Override
    public void initialize(ValidateRequestText constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.length() >= 3 && value.matches(".*[a-zA-Zа-яА-ЯёЁ].*");
    }
}
