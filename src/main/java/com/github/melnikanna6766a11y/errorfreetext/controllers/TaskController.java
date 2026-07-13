package com.github.melnikanna6766a11y.errorfreetext.controllers;

import com.github.melnikanna6766a11y.errorfreetext.dto.TaskResponse;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;

    @PostMapping("/task")
    public long saveTask(@RequestBody String text, @RequestBody long language_id) {
        return taskService.saveTask(text, language_id);
    }

//    @GetMapping("/task/{id}")
//    public TaskResponse getTask(@PathVariable long id) {
//
//    }
}
