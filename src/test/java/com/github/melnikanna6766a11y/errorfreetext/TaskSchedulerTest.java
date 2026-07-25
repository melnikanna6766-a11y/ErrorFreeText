package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.dto.TaskRequest;
import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.CounterOverflowException;
import com.github.melnikanna6766a11y.errorfreetext.repositories.LanguageRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskScheduler;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskService;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.LimitsHandler;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.SpellerInvoker;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.RequestHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = ErrorFreeTextApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TaskSchedulerTest {

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
        TaskScheduler taskScheduler = new TaskScheduler(taskRepository, limitsHandler, requestHandler);
        Mockito.when(limitsHandler.areCountersBelowTheLimits()).thenReturn(true);
        Mockito.when(limitsHandler.calculateRemainingChars()).thenReturn(1000L);
        CheckTextsRequest checkTextsRequest = new CheckTextsRequest(new String[]{"карова"}, "ru", 0, 6);
        Mockito.when(requestHandler.generateSpellerRequest(Mockito.any())).thenAnswer(mock -> {
            task.setLastProcessedWordIndex(5);
            return checkTextsRequest;
        });
        taskScheduler.handleTask();
    }

    @AfterEach
    public void deleteData() {
        taskRepository.delete(task);
    }
}
