package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.ServerExceptionHandler;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.LimitsHandler;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.SpellerInvoker;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.RequestHandler;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.TaskWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class TaskScheduler {
    private final TaskRepository taskRepository;
    private final LimitsHandler limitsHandler;
    private final RequestHandler requestHandler;
    private final ServerExceptionHandler serverExceptionHandler;

    @Scheduled(fixedRateString = "${fixed.rate}")
    @Async
    public void handleTask() {
        if(limitsHandler.areCountersBelowTheLimits()) {
            List<Task> tasks = taskRepository.findUnhandledTasks();
            tasks.forEach(task -> task.setStatus(Status.inProgress));
            saveStatus(tasks);
            log.info("Starting handle {} tasks ", tasks.size());
            for (Task task : tasks) {
                if (limitsHandler.areCountersBelowTheLimits()) {
                    Task performedTask = performSpellerRequest(task);
                    saveFinishedTask(performedTask);
                }
            }
            log.info("Finishing handle {} tasks from current scheduler call", tasks.size());
        }
    }

    @Transactional
    public void saveStatus(List<Task> task) {
        taskRepository.saveAll(task);
    }

    private Task performSpellerRequest(Task task) {
        log.info("Creating and send request to speller for task with id {}", task.getId());
        TaskWrapper taskWrapper = new TaskWrapper(task);
        List<List<SpellerResponse>> spellerResponse = new ArrayList<>();
        if (task.getSpellerResponses() != null) {
            spellerResponse.addAll(task.getSpellerResponses());
        }
        while (taskWrapper.lessThanRequestLengthWasWritten() && limitsHandler.calculateRemainingChars() > 0) {
            CheckTextsRequest request = requestHandler.generateSpellerRequest(task);
            if (!taskWrapper.haveError()) {
                if (limitsHandler.updateCounter(request)) {
                    spellerResponse.addAll(Objects.requireNonNull(sendRequestToSpeller(request, task)));
                    task.setSpellerResponses(spellerResponse);
                } else {
                    serverExceptionHandler.handleCounterOverflowException();
                    throw new CounterOverflowException("The number of requests per day or the number of characters has exceeded the limit");
                }
            }
        }
        log.info("Created speller response for task with id {}, count of requests {}", task.getId(), spellerResponse.size());
        return task;
    }

    private List<List<SpellerResponse>> sendRequestToSpeller(CheckTextsRequest request, Task task) {
        SpellerInvoker spellerInvoker = new SpellerInvoker();
        List<List<SpellerResponse>> spellerResponse = spellerInvoker.sendRequestToSpeller(request, task, serverExceptionHandler);
        return spellerInvoker.composeResponseFromSpeller(spellerResponse);
    }

    @Transactional
    public void saveFinishedTask(Task task) {
        TaskWrapper taskWrapper = new TaskWrapper(task);
        log.info("Starting set fields and saving task with id {}", task.getId());
        if (taskWrapper.lessThanRequestLengthWasWritten()) {
            task.setStatus(Status.incompleted);
        } else {
            task.setStatus(Status.completed);
        }
        task.setCompletionDate(LocalDate.now());
        taskRepository.save(task);
        log.info("Finishing saving task with id {} with status {}", task.getId(), task.getStatus());
    }
}
