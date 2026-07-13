package com.github.melnikanna6766a11y.errorfreetext.exceptions;

public class NoSuchIdException extends RuntimeException {
    public NoSuchIdException() {
        super("No element with id");
    }

    public NoSuchIdException(String message) {
        super(message);
    }
}
