package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.controllers.TaskController;
import com.github.melnikanna6766a11y.errorfreetext.dto.TaskRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.TaskResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.NoSuchIdException;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@ContextConfiguration(classes = ErrorFreeTextApplication.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    public void saveTaskTest() throws Exception {
        TaskRequest task = Mockito.mock(TaskRequest.class);
        Mockito.when(task.text()).thenReturn("test");
        Mockito.when(task.lang()).thenReturn("en");
        Mockito.when(taskService.saveTask(task)).thenReturn(UUID.randomUUID());
        String json = new ObjectMapper().writeValueAsString(task);
        mockMvc.perform(post("/tasks").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getTaskTest() throws Exception {
        UUID id = UUID.randomUUID();
        TaskResponse taskResponse = Mockito.mock(TaskResponse.class);
        Mockito.when(taskResponse.status()).thenReturn(Status.created);
        Mockito.when(taskResponse.responses()).thenReturn(new ArrayList<>());
        Mockito.when(taskService.findTaskById(id)).thenReturn(taskResponse);
        String json = new ObjectMapper().writeValueAsString(taskResponse);
        mockMvc.perform(get("/tasks/"+id))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    public void getTaskIdExceptionTest() throws Exception {
        UUID id = UUID.randomUUID();
        TaskResponse taskResponse = Mockito.mock(TaskResponse.class);
        Mockito.when(taskResponse.status()).thenReturn(Status.created);
        Mockito.when(taskResponse.responses()).thenReturn(new ArrayList<>());
        Mockito.when(taskService.findTaskById(id)).thenThrow(new NoSuchIdException("Task with id: "+ id + " not found"));
        mockMvc.perform(get("/tasks/"+id))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void saveTaskBadRequestTest() throws Exception {
        TaskRequest task = Mockito.mock(TaskRequest.class);
        Mockito.when(task.text()).thenReturn("456$%^&");
        Mockito.when(task.lang()).thenReturn("hj");
        Mockito.when(taskService.saveTask(task)).thenReturn(UUID.randomUUID());
        String json = new ObjectMapper().writeValueAsString(task);
        mockMvc.perform(post("/tasks").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}
