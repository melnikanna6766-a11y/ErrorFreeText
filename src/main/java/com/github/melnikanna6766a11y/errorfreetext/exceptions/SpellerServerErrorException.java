package com.github.melnikanna6766a11y.errorfreetext.exceptions;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import lombok.Getter;

@Getter
public class SpellerServerErrorException extends RuntimeException {
    private final Task task;

    public SpellerServerErrorException(String message, Task task) {
        super(message);
        this.task = task;
    }
}
