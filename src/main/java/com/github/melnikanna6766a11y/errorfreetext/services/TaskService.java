package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.dto.TaskRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.TaskResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.LanguageRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Log4j2
public class TaskService {
    private TaskRepository taskRepository;
    private LanguageRepository languageRepository;

    @Transactional
    public UUID saveTask(TaskRequest task) {
        log.info("Processing the task ({}) for saving", task.text());
        Task currentTask = new Task();
        currentTask.setInputText(task.text());
        currentTask.setLastProcessedWordIndex(0);
        currentTask.setStatus(Status.created);
        currentTask.setLanguage(languageRepository.findByLanguage(task.lang()).orElseThrow(() -> new NoSuchIdException(Language.class, task.lang())));
        log.debug("saving the task: lang = {}, status = {}",
                currentTask.getLanguage(),
                currentTask.getStatus()
        );
        return taskRepository.save(currentTask).getId();
    }

    public TaskResponse findTaskById(UUID id) {
        log.info("Find task by id {}", id);
        Task task = taskRepository.findById(id).orElseThrow(() -> new NoSuchIdException(Task.class, id));
        return new TaskResponse(task.getSpellerResponses(), task.getStatus(), null);
    }

    public UUID saveStatusFor(Status status, Task task) {
        task.setStatus(status);
        return taskRepository.save(task).getId();
    }
}
