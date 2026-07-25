package com.github.melnikanna6766a11y.errorfreetext.services.helpers;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class TaskWrapper {
    private final Task task;
    private long numberOfCharacters;
    private long arrayLength;

    public TaskWrapper(Task task) {
        this.task = task;
        this.numberOfCharacters = Arrays.stream(task.getInputText().split("[,\\s]+"))
                .mapToLong(String::length)
                .sum();
        this.arrayLength = task.getInputText().split("[,\\s]+").length;
    }

    public boolean lessThanRequestLengthWasWritten() {
        return task.getLastProcessedWordIndex() < arrayLength;
    }
}
