package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.CheckTextsResponseHandler;
import com.github.melnikanna6766a11y.errorfreetext.RequestSender;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import lombok.AllArgsConstructor;
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

    @Scheduled(fixedRate = 3600000)
    @Async
    public void handleTask() {
        List<Task> tasks = taskRepository.findAllTasksWhereCreated();
        for (Task task: tasks) {
            RequestSender requestSender = new RequestSender();
            task.setStatus(statusRepository.findById(2L).orElseThrow(NoSuchIdException::new));
            taskRepository.save(task);
            List<String> jsons = new CheckTextsResponseHandler().createCheckTextResponse(task);
            List<CorrectedTextResponse> correctedTextResponses = new ArrayList<>();
            for (String json : jsons) {
                CorrectedTextResponse correctedTextResponse;
                if ((correctedTextResponse = requestSender.sendRequest(json)) != null) {
                    correctedTextResponses.add(correctedTextResponse);
                } else {
                    task.setStatus(statusRepository.findById(4L).orElseThrow(NoSuchIdException::new));
                    taskRepository.save(task);
                    break;
                }
            }
            task.setResponse(correctedTextResponses);
            task.setStatus(statusRepository.findById(3L).orElseThrow(NoSuchIdException::new));
            taskRepository.save(task);
        }
    }
}
