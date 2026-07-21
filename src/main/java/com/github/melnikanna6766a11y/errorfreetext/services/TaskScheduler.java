package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.services.helpers.RequestSender;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.ResponseHandler;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Log4j2
@RequiredArgsConstructor
public class TaskScheduler {
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final ResponseHandler responseHandler;
    @Value("${chars.limit}")
    private int charsLimit;
    @Value("${execution.limit}")
    private int executionLimit;
    private final ConcurrentHashMap<UUID, Task> processingTasks = new ConcurrentHashMap<>();

    @Scheduled(fixedRateString = "${fixed.rate}")
    @Async
    public void handleTask() {
        List<Task> tasks = taskRepository.findAllCreatedTasks();
        log.info("Starting handle {} tasks ", tasks.size());
        for (Task task : tasks) {
            taskProcessing(task);
        }
        log.info("Finishing handle {} tasks from current scheduler call", tasks.size());
    }

    @Transactional
    private void taskProcessing(Task task) {
        log.info("Starting processing task with id {}", task.getId());
        Status inProgress = statusRepository.findById(2L).orElseThrow(() -> new NoSuchIdException(Status.class, 2L));
        Integer dayCharsCounter = taskRepository.calculateSendedCharsLastDay(LocalDate.now());
        Integer dayExecutionsCounter = taskRepository.calculateExecutionsLastDay(LocalDate.now());
        if (dayCharsCounter == null && dayExecutionsCounter == null) {
            dayCharsCounter = 0;
            dayExecutionsCounter = 0;
        }
        if (dayCharsCounter > charsLimit || dayExecutionsCounter  > executionLimit) {
            throw new CounterOverflowException("The number of requests per day or the number of characters has exceeded the limit");
        }
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            if (processingTasks.putIfAbsent(task.getId(), task) == null) {
                task.setStatus(inProgress);
                taskRepository.save(task);
                long statusId = responseHandler.createCorrectedTextResponse(task, new RequestSender(), charsLimit - dayCharsCounter);
                task.setStatus(statusRepository.findById(statusId).orElseThrow(() -> new NoSuchIdException(Status.class, statusId)));
                task.setCompletionDate(LocalDate.now());
                taskRepository.save(task);
                log.info("Finishing processing task with id {} with status {}", task.getId(), task.getStatus().getStatus());
            }
        } finally {
            lock.unlock();
        }
    }
}
