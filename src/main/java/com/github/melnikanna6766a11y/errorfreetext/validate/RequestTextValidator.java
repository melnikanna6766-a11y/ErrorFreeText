package com.github.melnikanna6766a11y.errorfreetext.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RequestTextValidator implements ConstraintValidator<ValidateRequestText, String> {
    @Override
    public void initialize(ValidateRequestText constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.info("validate request text {}", value);
        return value.length() >= 3 && value.matches(".*[a-zA-Zа-яА-ЯёЁ].*");
    }
}
