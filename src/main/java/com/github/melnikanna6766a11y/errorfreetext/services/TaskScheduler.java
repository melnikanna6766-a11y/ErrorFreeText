package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskCriteriaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskScheduler {
    private TaskCriteriaRepository taskCriteriaRepository;

    @Scheduled(fixedRate = 2000)
    public void handleTask() {
        Task task = taskCriteriaRepository.findEarliestCreatedTask();
    }
}
