package com.github.melnikanna6766a11y.errorfreetext.exceptions;

import java.util.UUID;

public class NoSuchIdException extends RuntimeException {

    public NoSuchIdException() {
        super("No element with id");
    }

    public NoSuchIdException(Class<?> clas, Object id) {
        super(clas.getSimpleName() + " with id: " + id + " not found");
    }

    public NoSuchIdException(String message) {
        super(message);
    }
}
