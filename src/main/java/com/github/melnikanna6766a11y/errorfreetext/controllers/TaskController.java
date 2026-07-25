package com.github.melnikanna6766a11y.errorfreetext.controllers;

import com.github.melnikanna6766a11y.errorfreetext.dto.TaskRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.TaskResponse;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Log4j2
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/tasks")
    public UUID saveTask(@Valid @RequestBody TaskRequest task) {
        log.info(
                "Receiving a POST request by URL {} with body text: {}, language: {}",
                ServletUriComponentsBuilder.fromCurrentRequest().build().getPath(),
                task.text(),
                task.lang()
        );
        return taskService.saveTask(task);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@PathVariable UUID id) {
        log.info(
                "Receiving a GET request by URL {}",
                ServletUriComponentsBuilder.fromCurrentRequest().build().getPath()
        );
        return taskService.findTaskById(id);
    }
}
