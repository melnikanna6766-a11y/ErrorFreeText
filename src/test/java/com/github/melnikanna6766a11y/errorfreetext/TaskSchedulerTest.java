package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.StatusRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = ErrorFreeTextApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TaskSchedulerTest {

    @MockitoBean
    private TaskRepository taskRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Test
    public void handleTaskTest() {
        Mockito.when(taskRepository.findSumSentChars(LocalDate.now())).thenReturn(2345);
        Mockito.when(taskRepository.findSumSentExecutions(LocalDate.now())).thenReturn(3);
        List<Task> tasks = new ArrayList<>();
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getInputText()).thenReturn("qwerty");
        Mockito.when(task.getNumberOfCharacters()).thenReturn(6);
        Mockito.when(task.getNumberOfExecutions()).thenReturn(1);
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        tasks.add(task);
        Mockito.when(taskRepository.findAllCreatedTasks()).thenReturn(tasks);
        TaskScheduler taskScheduler = new TaskScheduler(taskRepository, statusRepository);
        taskScheduler.handleTask();
    }

    @Test
    public void handleTaskOverflowCounterTest() {
        Mockito.when(taskRepository.findSumSentChars(LocalDate.now())).thenReturn(10000000);
        Mockito.when(taskRepository.findSumSentExecutions(LocalDate.now())).thenReturn(10000);
        List<Task> tasks = new ArrayList<>();
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getInputText()).thenReturn("qwerty");
        Mockito.when(task.getNumberOfCharacters()).thenReturn(6);
        Mockito.when(task.getNumberOfExecutions()).thenReturn(1);
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        tasks.add(task);
        Mockito.when(taskRepository.findAllCreatedTasks()).thenReturn(tasks);
        TaskScheduler taskScheduler = new TaskScheduler(taskRepository, statusRepository);
        assertThrows(CounterOverflowException.class, taskScheduler::handleTask);

    }

    @Test
    public void handleTaskErrorTest() {
        Mockito.when(taskRepository.findSumSentChars(LocalDate.now())).thenReturn(235);
        Mockito.when(taskRepository.findSumSentExecutions(LocalDate.now())).thenReturn(10);
        List<Task> tasks = new ArrayList<>();
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getInputText()).thenReturn("qwerty");
        Mockito.when(task.getNumberOfCharacters()).thenReturn(6);
        Mockito.when(task.getNumberOfExecutions()).thenReturn(1);
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        tasks.add(task);
        Mockito.when(taskRepository.findAllCreatedTasks()).thenReturn(tasks);
        TaskScheduler taskScheduler = new TaskScheduler(taskRepository, statusRepository);
        ResponseHandler responseHandler = new ResponseHandler();
        responseHandler.createCorrectedTextResponse(task);
        taskScheduler.handleTask();
    }
}
