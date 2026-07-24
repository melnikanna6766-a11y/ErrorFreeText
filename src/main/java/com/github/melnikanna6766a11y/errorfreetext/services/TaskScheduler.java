package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.LimitsHandler;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.SpellerRequestSender;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Log4j2
@RequiredArgsConstructor
public class TaskScheduler {
    private final TaskRepository taskRepository;
    private final LimitsHandler limitsHandler;
    private final RequestHandler requestHandler;

    private Lock lock = new ReentrantLock();

    @Scheduled(fixedRateString = "${fixed.rate}")
    @Async
    public void handleTask() {
        List<Task> tasks = taskRepository.findUnhandledTasks();
        tasks.forEach(task -> task.setStatus(Status.inProgress));
        saveStatus(tasks);
        log.info("Starting handle {} tasks ", tasks.size());
        for (Task task : tasks) {
            lock.lock();
            try {
            TaskWrapper taskWrapper = new TaskWrapper(task);
            saveResponseToTask(taskWrapper, performSpellerRequest(taskWrapper));
            limitsHandler.updateCounter(taskWrapper);
            } finally {
                lock.unlock();
            }
        }
        log.info("Finishing handle {} tasks from current scheduler call", tasks.size());
    }

    @Transactional
    public void saveStatus(List<Task> task) {
        taskRepository.saveAll(task);
    }

    @Transactional
    public void saveResponseToTask(TaskWrapper task, List<List<SpellerResponse>> spellerResponse) {
        log.info("Starting processing task with id {}", task.getTask().getId());
        if (task.getTask().getSpellerResponses() != null) {
            spellerResponse.addAll(task.getTask().getSpellerResponses());
        }
        task.getTask().setSpellerResponses(spellerResponse);
        task.getTask().setCompletionDate(LocalDate.now());
        taskRepository.save(task.getTask());
        log.info("Finishing processing task with id {} with status {}", task.getTask().getId(), task.getTask().getStatus());
    }

    private List<List<SpellerResponse>> performSpellerRequest(TaskWrapper task) {
        List<List<SpellerResponse>> spellerResponse = new ArrayList<>();
        int writtenChars = 0;
        while (
                task.getTask().getLastProcessedWordIndex() < task.getArrayLength()
                        && writtenChars < limitsHandler.calculateNumberOfRemainingCharacters()
        ) {
            CheckTextsRequest request = requestHandler.generateSpellerRequest(task.getTask());
            List<List<SpellerResponse>> spellerResponseList;
            if ((spellerResponseList = sendRequestToSpeller(request, task.getTask())) != null) {
                spellerResponse.addAll(spellerResponseList);
            } else {
                task.getTask().setStatus(Status.error);
            }
            writtenChars += Arrays.stream(request.text()).mapToInt(String::length).sum();
        }
        task.getTask().setLastProcessedWordIndex(task.getTask().getLastProcessedWordIndex() - 1);
        log.info("Created check text request for task with id {}, count of requests", task.getTask().getId());
        if (task.getTask().getLastProcessedWordIndex() < task.getArrayLength()) {
            task.getTask().setStatus(Status.incompleted);
        } else {
            task.getTask().setStatus(Status.completed);
        }
        return spellerResponse;
    }


    private List<List<SpellerResponse>> sendRequestToSpeller(CheckTextsRequest request, Task task) {
        SpellerRequestSender spellerRequestSender = new SpellerRequestSender();
        List<List<SpellerResponse>> spellerResponses = new ArrayList<>();
        List<List<SpellerResponse>> spellerResponse;
        if ((spellerResponse = spellerRequestSender.sendRequest(request, task)) != null) {
            spellerResponses.add(
                    spellerResponse.stream()
                            .filter(response -> !response.isEmpty())
                            .map(List::removeFirst)
                            .toList()
            );
        } else {
            log.info("Response for task, was not created because no response body was available");
            return null;
        }
        return spellerResponses;
    }
}
