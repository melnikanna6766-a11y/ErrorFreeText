package com.github.melnikanna6766a11y.errorfreetext.controllers;

import com.github.melnikanna6766a11y.errorfreetext.dto.TaskResponse;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;

    @PostMapping("/tasks")
    public UUID saveTask(@RequestBody String text, @RequestBody long language_id) {
        return taskService.saveTask(text, language_id);
    }

//    @GetMapping("/tasks/{id}")
//    public TaskResponse getTask(@PathVariable long id) {
//
//    }
}
