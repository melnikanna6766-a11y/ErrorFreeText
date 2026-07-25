package com.github.melnikanna6766a11y.errorfreetext.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= RequestTextValidator.class)
public @interface ValidateRequestText {
    String message() default "The text must contain more than 3 characters and include more than just special characters and numbers";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
