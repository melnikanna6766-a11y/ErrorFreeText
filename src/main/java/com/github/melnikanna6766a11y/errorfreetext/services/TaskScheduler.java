package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.ResponseHandler;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskScheduler {
    private final TaskRepository taskRepository;
    private final Status inProgress;
    private final Status completed;
    private final Status error;
    private static final int CHARS_LIMIT = 10_000_000;
    private static final int EXECUTION_LIMIT = 10_000;

    public TaskScheduler(TaskRepository taskRepository, StatusRepository statusRepository) {
        this.taskRepository = taskRepository;
        inProgress = statusRepository.findById(2L).orElseThrow(() -> new NoSuchIdException(Status.class, 2L));
        completed = statusRepository.findById(3L).orElseThrow(() -> new NoSuchIdException(Status.class, 3L));
        error =  statusRepository.findById(4L).orElseThrow(() -> new NoSuchIdException(Status.class, 4L));
    }

    @Scheduled(fixedDelay = 60000)
    public void handleTask() {
        List<Task> tasks = taskRepository.findAllCreatedTasks();
        for (Task task: tasks) {
            taskProcessing(task);
        }
    }

    @Transactional
    private void taskProcessing(Task task) {
        Integer countOfToDayChars = taskRepository.findSumSentChars(LocalDate.now());
        Integer countOfToDayExecutions = taskRepository.findSumSentExecutions(LocalDate.now());
        if (countOfToDayChars + task.getNumberOfCharacters() < CHARS_LIMIT || countOfToDayExecutions + task.getNumberOfExecutions() < EXECUTION_LIMIT) {
            task.setStatus(inProgress);
            taskRepository.save(task);
            boolean isCompleted = new ResponseHandler().createCorrectedTextResponse(task);
            if (isCompleted) {
                task.setCompletionDate(LocalDate.now());
                task.setStatus(completed);
            } else {
                task.setStatus(error);
            }
            taskRepository.save(task);
        } else {
            throw new CounterOverflowException("The number of requests per day or the number of characters has exceeded the limit");
        }
    }
}
