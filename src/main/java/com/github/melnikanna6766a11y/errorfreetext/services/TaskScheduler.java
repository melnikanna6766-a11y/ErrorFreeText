package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.RequestSender;
import com.github.melnikanna6766a11y.errorfreetext.ResponseHandler;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Log4j2
public class TaskScheduler {
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private static final int CHARS_LIMIT = 10_000_000;
    private static final int EXECUTION_LIMIT = 10_000;

    public TaskScheduler(TaskRepository taskRepository, StatusRepository statusRepository) {
        this.taskRepository = taskRepository;
        this.statusRepository = statusRepository;
    }

    @Scheduled(fixedDelayString = "${fixed.rate}")
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
        Status completed = statusRepository.findById(3L).orElseThrow(() -> new NoSuchIdException(Status.class, 3L));
        Status error = statusRepository.findById(4L).orElseThrow(() -> new NoSuchIdException(Status.class, 4L));
        Integer countOfToDayChars = taskRepository.findSumSentChars(LocalDate.now());
        Integer countOfToDayExecutions = taskRepository.findSumSentExecutions(LocalDate.now());
        if (countOfToDayChars == null && countOfToDayExecutions == null) {
            countOfToDayChars = 0;
            countOfToDayExecutions = 0;
        }
        if (countOfToDayChars + task.getNumberOfCharacters() > CHARS_LIMIT ||
                        countOfToDayExecutions + task.getNumberOfExecutions() > EXECUTION_LIMIT) {
            throw new CounterOverflowException("The number of requests per day or the number of characters has exceeded the limit");
        }
        task.setStatus(inProgress);
        taskRepository.save(task);
        log.info("");
        boolean isCompleted = new ResponseHandler().createCorrectedTextResponse(task, new RequestSender());
        if (isCompleted) {
            task.setCompletionDate(LocalDate.now());
            task.setStatus(completed);
        } else {
            task.setStatus(error);
        }
        taskRepository.save(task);
        log.info("Finishing processing task with id {} with status {}", task.getId(), task.getStatus().getStatus());
    }
}
