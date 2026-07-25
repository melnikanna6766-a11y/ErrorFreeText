package com.github.melnikanna6766a11y.errorfreetext.validate;

import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= LanguageValidator.class)
public @interface ValidateLang {
    String message() default "The language can only be 'ru' or 'en'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
