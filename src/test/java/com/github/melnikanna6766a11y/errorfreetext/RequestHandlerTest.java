package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.LimitsHandler;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.RequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ErrorFreeTextApplication.class)
@ActiveProfiles("test")
public class RequestHandlerTest {
    @Value("${request.limit}")
    private long requestLimit;
    @Value("${chars.limit}")
    private long charsLimit;
    @MockitoBean
    LimitsHandler limitsHandler;
    @Mock
    Task task;
    @Mock
    Language language;

    @BeforeEach
    public void before() {
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        Mockito.when(limitsHandler.getRequestLimit()).thenReturn(requestLimit);
        Mockito.when(limitsHandler.calculateRemainingChars()).thenReturn(charsLimit);
    }

    @Test
    public void generateSpellerRequestTest() {
        Mockito.when(task.getInputText()).thenReturn("meow");
        RequestHandler requestHandler = new RequestHandler(limitsHandler);
        CheckTextsRequest request = requestHandler.generateSpellerRequest(task);
        assertEquals("meow", request.text()[0]);
        assertEquals(4, request.charsNumber());
        assertEquals(0, request.option());
    }

    @Test
    public void createCheckTextRequestWithUrlTest() {
        Mockito.when(task.getInputText()).thenReturn("https://yandex.ru/dev/speller/doc/ru/reference/checkTexts yandex56");
        RequestHandler requestHandler = new RequestHandler(limitsHandler);
        CheckTextsRequest request = requestHandler.generateSpellerRequest(task);
        assertEquals("https://yandex.ru/dev/speller/doc/ru/reference/checkTexts", request.text()[0]);
        assertEquals("yandex56", request.text()[1]);
        assertEquals(2, request.text().length);
        assertEquals(65, request.charsNumber());
        assertEquals(6, request.option());
    }
}
