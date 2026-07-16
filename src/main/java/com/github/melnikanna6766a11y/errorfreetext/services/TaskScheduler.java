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
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskScheduler {
    private TaskRepository taskRepository;
    private StatusRepository statusRepository;


    @Scheduled(fixedRateString = "${fixed.rate}")
    @Async
    public void handleTask() {
        List<Task> tasks = taskRepository.findAllCreatedTasks();
        for (Task task: tasks) {
            RequestSender requestSender = new RequestSender();
            task.setStatus(statusRepository.findById(2L).orElseThrow(() -> new NoSuchIdException(Status.class, 2L)));
            taskRepository.save(task);
            List<CheckTextsResponse> responses = new CheckTextsResponseHandler().createCheckTextResponse(task);
            List<CorrectedTextResponse> correctedTextResponses = new ArrayList<>();
            for (CheckTextsResponse response : responses) {
                List<List<CorrectedTextResponse>> correctedTextResponse;
                if ((correctedTextResponse = requestSender.sendRequest(response)) != null) {
                    for (List<CorrectedTextResponse> correctedTextResponseList: correctedTextResponse) {
                        correctedTextResponses.addAll(correctedTextResponseList);
                    }
                } else {
                    task.setStatus(statusRepository.findById(4L).orElseThrow(() -> new NoSuchIdException(Status.class, 4L)));
                    taskRepository.save(task);
                    break;
                }
            }
            task.setResponse(correctedTextResponses);
            task.setStatus(statusRepository.findById(3L).orElseThrow(() -> new NoSuchIdException(Status.class, 3L)));
            taskRepository.save(task);
        }
    }
}
