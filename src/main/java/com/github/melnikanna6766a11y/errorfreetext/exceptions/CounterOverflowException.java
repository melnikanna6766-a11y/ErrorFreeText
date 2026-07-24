package com.github.melnikanna6766a11y.errorfreetext.exceptions;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import lombok.Getter;

import java.util.List;

@Getter
public class CounterOverflowException extends RuntimeException {
    private List<Task> uncompletedTasks;

    public CounterOverflowException(String message, List<Task> uncompletedTasks) {
        this.uncompletedTasks = uncompletedTasks;
        super(message);
    }

    public CounterOverflowException(String message) {
        super(message);
    }
}
