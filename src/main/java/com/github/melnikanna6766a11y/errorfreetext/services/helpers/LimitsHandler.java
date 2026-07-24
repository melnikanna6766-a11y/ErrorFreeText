package com.github.melnikanna6766a11y.errorfreetext.services.helpers;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
public class LimitsHandler {
    @Value("${chars.limit}")
    private int charsLimit;
    @Value("${execution.limit}")
    private int executionLimit;
    @Value("${request.limit}")
    private int requestLimit;
    private final TaskRepository taskRepository;
    private AtomicLong dayCharsCounter = new AtomicLong();
    private AtomicLong dayExecutionsCounter = new AtomicLong();
    private LocalDate dateOfLastAccess = LocalDate.now();

    // у нас 3 варианта:
    // - приложение не запускалось тогда: счетчики 0, дата - сегодня
    // - приложение запускалось не сегодня: счетчики 0, дата - сегодня
    // - приложение запускалось сегодня: дата - сегодня и нужны цифры для счетчиков ....

    public LimitsHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
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
    }
    
    public void updateCounter(TaskWrapper task) {
        if (areCountersBelowTheLimits()) {
            throw new CounterOverflowException("The number of requests per day or the number of characters has exceeded the limit");
        }
        if (!dateOfLastAccess.equals(LocalDate.now())) {
            dayCharsCounter.set(0);
            dayExecutionsCounter.set(0);
        }
        dayCharsCounter.addAndGet(task.getNumberOfCharacters());
        dayExecutionsCounter.addAndGet(task.getTask().getSpellerResponses().size());
        dateOfLastAccess = LocalDate.now();
    }

    public boolean areCountersBelowTheLimits() {
        return dayCharsCounter.get() > charsLimit || dayExecutionsCounter.get() > executionLimit;
    }

    public long calculateNumberOfRemainingCharacters() {
        return charsLimit - dayCharsCounter.get();
    }
}
