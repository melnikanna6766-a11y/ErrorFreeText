package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.dto.TaskRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.TaskResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
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
        currentTask.setNumberOfCharacters(task.text().length());
        currentTask.setNumberOfExecutions((task.text().length()+9999)/10000);
        currentTask.setStatus(statusRepository.findById(1L).orElseThrow(() -> new NoSuchIdException(Status.class, 1L)));
        currentTask.setLanguage(languageRepository.findByLanguage(task.lang()).orElseThrow(() -> new NoSuchIdException(Language.class, task.lang())));
        return taskRepository.save(currentTask).getId();
    }

    public TaskResponse findTaskById(UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NoSuchIdException(Task.class, id));
        return new TaskResponse(task.getResponse(), task.getStatus().getStatus());
    }
}
