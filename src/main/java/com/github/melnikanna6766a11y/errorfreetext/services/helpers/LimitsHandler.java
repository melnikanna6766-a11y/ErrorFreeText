package com.github.melnikanna6766a11y.errorfreetext.services.helpers;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
@Log4j2
public class LimitsHandler {
    @Value("${chars.limit}")
    private long charsLimit;
    @Value("${execution.limit}")
    private long executionLimit;
    @Value("${request.limit}")
    private long requestLimit;
    private final TaskRepository taskRepository;
    private AtomicLong dayCharsCounter = new AtomicLong();
    private AtomicLong dayExecutionsCounter = new AtomicLong();
    private LocalDate dateOfLastAccess = LocalDate.now();

    public LimitsHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        log.info("Counter initialization begins");
        List<Task> handledTasks = taskRepository.findHandledDayTasks(LocalDate.now());
        if (handledTasks.isEmpty()) {
            dayCharsCounter.set(0);
            dayExecutionsCounter.set(0);
        } else {
            handledTasks.stream()
                    .map(TaskWrapper::new)
                    .forEach(task -> {
                        dayCharsCounter.addAndGet(task.getNumberOfCharacters());
                        dayExecutionsCounter.addAndGet(task.getTask().getSpellerResponses().size());
                    });
        }
        log.info("Counter initialization has completed, dayCharsCounter = {}, dayExecutionsCounter = {}", dayCharsCounter, dayExecutionsCounter);
    }
    
    public boolean updateCounter(CheckTextsRequest request) {
        if (!areCountersBelowTheLimits()) {
            return false;
        }
        if (!dateOfLastAccess.equals(LocalDate.now())) {
            dayCharsCounter.set(0);
            dayExecutionsCounter.set(0);
        }
        dayCharsCounter.addAndGet(request.charsNumber());
        dayExecutionsCounter.addAndGet(1);
        dateOfLastAccess = LocalDate.now();
        return true;
    }

    public boolean areCountersBelowTheLimits() {
        return dayCharsCounter.get() <= charsLimit || dayExecutionsCounter.get() <= executionLimit;
    }

    public long calculateRemainingChars() {
        return charsLimit - dayCharsCounter.get();
    }
}
