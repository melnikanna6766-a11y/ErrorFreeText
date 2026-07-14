package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.LanguageRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {
    private TaskRepository taskRepository;
    private StatusRepository statusRepository;
    private LanguageRepository languageRepository;

    public long saveTask(String text, long language_id) {
        Task currentTask = new Task();
        currentTask.setText(text);
        currentTask.setLanguage(languageRepository.findById(language_id).orElseThrow(NoSuchIdException::new));
        Task task = taskRepository.save(currentTask);
        return task.getId();
    }


}
