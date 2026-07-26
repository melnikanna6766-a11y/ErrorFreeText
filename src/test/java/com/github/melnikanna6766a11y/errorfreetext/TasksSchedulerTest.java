package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.repositories.LanguageRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import com.github.melnikanna6766a11y.errorfreetext.services.TasksScheduler;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.LimitsHandler;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.RequestHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@ContextConfiguration(classes = ErrorFreeTextApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TasksSchedulerTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @MockitoBean
    private RequestHandler requestHandler;

    @MockitoBean
    private LimitsHandler limitsHandler;

    private final Task task = new Task();

    @BeforeEach
    public void insertData() {
        task.setInputText("карова");
        task.setStatus(Status.created);
        task.setLanguage(languageRepository.findByLanguage("ru").orElseThrow());
        task.setLastProcessedWordIndex(0);
        taskRepository.save(task);
    }

    @Test
    public void handleTaskTest() {
        TasksScheduler tasksScheduler = new TasksScheduler(taskRepository, limitsHandler, requestHandler);
        Mockito.when(limitsHandler.areCountersBelowTheLimits()).thenReturn(true);
        Mockito.when(limitsHandler.calculateRemainingChars()).thenReturn(1000L);
        CheckTextsRequest checkTextsRequest = new CheckTextsRequest(new String[]{"карова"}, "ru", 0, 6);
        Mockito.when(requestHandler.generateSpellerRequest(Mockito.any())).thenAnswer(mock -> {
            task.setLastProcessedWordIndex(5);
            return checkTextsRequest;
        });
        tasksScheduler.handleTask();
    }

    @AfterEach
    public void deleteData() {
        taskRepository.delete(task);
    }
}
