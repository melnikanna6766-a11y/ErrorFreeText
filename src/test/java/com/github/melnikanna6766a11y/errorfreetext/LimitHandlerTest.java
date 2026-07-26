package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Status;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.repositories.LanguageRepository;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskRepository;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.LimitsHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ErrorFreeTextApplication.class)
@ActiveProfiles("test")
public class LimitHandlerTest {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LimitsHandler limitsHandler;

    private Task task = new Task();

    @BeforeEach
    public void insertData() {
        task.setInputText("карова");
        List<List<SpellerResponse>> spellerResponseList = new ArrayList<>();
        List<SpellerResponse> innerList = new ArrayList<>();
        innerList.add(new SpellerResponse(0, 0, 0, 0, 0, "карова", new ArrayList<>()));
        spellerResponseList.add(innerList);
        task.setSpellerResponses(spellerResponseList);
        task.setStatus(Status.completed);
        task.setLastProcessedWordIndex(5);
        task.setCompletionDate(LocalDate.now());
        task.setLanguage(languageRepository.findByLanguage("ru").orElseThrow());
        taskRepository.save(task);
    }

    @Test
    public void initCounterTest() {
        long dayCharsCounter = limitsHandler.getDayCharsCounter().get();
        long dayExecutionsCounter = limitsHandler.getDayExecutionsCounter().get();
        limitsHandler.init();
        assertEquals(dayCharsCounter + 6, limitsHandler.getDayCharsCounter().get());
        assertEquals(dayExecutionsCounter + 1, limitsHandler.getDayExecutionsCounter().get());
    }

    @Test
    public void updateCounterTest() {
        long dayCharsCounter = limitsHandler.getDayCharsCounter().get();
        long dayExecutionsCounter = limitsHandler.getDayExecutionsCounter().get();
        CheckTextsRequest checkTextsRequest = new CheckTextsRequest(new String[]{"карова"}, "ru", 0, 6);
        limitsHandler.updateCounter(checkTextsRequest);
        assertEquals(dayCharsCounter + 6, limitsHandler.getDayCharsCounter().get());
        assertEquals(dayExecutionsCounter + 1, limitsHandler.getDayExecutionsCounter().get());
    }

    @AfterEach
    public void deleteData() {
        taskRepository.delete(task);
    }
}
