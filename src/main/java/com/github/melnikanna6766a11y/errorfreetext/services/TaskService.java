package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.dto.TaskRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.TaskResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.LanguageRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskService {
    private TaskRepository taskRepository;
    private StatusRepository statusRepository;
    private LanguageRepository languageRepository;

    @Transactional
    public UUID saveTask(TaskRequest task) {
        Task currentTask = new Task();
        currentTask.setInputText(task.text());
        currentTask.setStatus(statusRepository.findById(1L).orElseThrow(NoSuchIdException::new));
        currentTask.setLanguage(languageRepository.findByLanguage(task.lang()).orElseThrow(NoSuchIdException::new));
        return taskRepository.save(currentTask).getId();
    }

    public TaskResponse findTaskById(UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(NoSuchIdException::new);
        return new TaskResponse(task.getResponse(), task.getStatus().getStatus());
    }
}
