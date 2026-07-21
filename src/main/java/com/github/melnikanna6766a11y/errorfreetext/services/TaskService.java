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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@Log4j2
public class TaskService {
    private TaskRepository taskRepository;
    private StatusRepository statusRepository;
    private LanguageRepository languageRepository;

    @Transactional
    public UUID saveTask(TaskRequest task) {
        log.info("Processing the task ({}) for saving", task.text());
        Task currentTask = new Task();
        currentTask.setInputText(task.text());
        AtomicInteger numberOfCharacters = new AtomicInteger();
        Arrays.stream(task.text().split(" ")).forEach(word -> numberOfCharacters.addAndGet(word.length()));
        currentTask.setNumberOfCharacters(numberOfCharacters.get());
        currentTask.setNumberOfExecutions((numberOfCharacters.get()+9999)/10000);
        currentTask.setNumberOfSavedElements(0);
        currentTask.setStatus(statusRepository.findById(1L).orElseThrow(() -> new NoSuchIdException(Status.class, 1L)));
        currentTask.setLanguage(languageRepository.findByLanguage(task.lang()).orElseThrow(() -> new NoSuchIdException(Language.class, task.lang())));
        log.debug("saving the task: lang = {}, status = {}, number of characters = {}, number of executions = {}",
                currentTask.getLanguage(),
                currentTask.getStatus(),
                currentTask.getNumberOfCharacters(),
                currentTask.getNumberOfExecutions()
        );
        return taskRepository.save(currentTask).getId();
    }

    public TaskResponse findTaskById(UUID id) {
        log.info("Find task by id {}", id);
        Task task = taskRepository.findById(id).orElseThrow(() -> new NoSuchIdException(Task.class, id));
        return new TaskResponse(task.getResponse(), task.getStatus().getStatus());
    }

    public UUID saveStatusErrorFor(UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NoSuchIdException(Task.class.getSimpleName() + " with id: " + id + " not found"));
        task.setStatus(statusRepository.findById(4L).orElseThrow(() -> new NoSuchIdException(Status.class.getSimpleName() + " with id: " + 4 + " not found")));
        return taskRepository.save(task).getId();
    }
}
