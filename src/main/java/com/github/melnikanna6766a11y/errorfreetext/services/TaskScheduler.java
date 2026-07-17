package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.CheckTextsResponseHandler;
import com.github.melnikanna6766a11y.errorfreetext.RequestSender;
import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsResponse;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
        int countOfToDayChars = taskRepository.findSumSentChars(LocalDate.now());
        int countOfToDayExecutions = taskRepository.findSumSentExecutions(LocalDate.now());
        for (Task task: tasks) {
            if (countOfToDayChars + task.getNumberOfCharacters() < CHARS_LIMIT || countOfToDayExecutions + task.getNumberOfExecutions() < EXECUTION_LIMIT) {
                task.setStatus(inProgress);
                taskRepository.save(task);
                boolean isCompleted = taskProcessing(task);
                if (isCompleted) {
                    task.setCompletionDate(LocalDate.now());
                    task.setStatus(completed);
                } else {
                    task.setStatus(error);
                }
                taskRepository.save(task);
            }
        }
    }

    @Transactional
    public boolean taskProcessing(Task task) {
        RequestSender requestSender = new RequestSender();
        List<CheckTextsResponse> responses = new CheckTextsResponseHandler().createCheckTextResponse(task);
        List<CorrectedTextResponse> correctedTextResponses = new ArrayList<>();
        for (CheckTextsResponse response : responses) {
            List<List<CorrectedTextResponse>> correctedTextResponse;
            if ((correctedTextResponse = requestSender.sendRequest(response)) != null) {
                for (List<CorrectedTextResponse> correctedTextResponseList: correctedTextResponse) {
                    correctedTextResponses.addAll(correctedTextResponseList);
                }
            } else {
                return false;
            }
        }
        task.setResponse(correctedTextResponses);
        return true;
    }
}
