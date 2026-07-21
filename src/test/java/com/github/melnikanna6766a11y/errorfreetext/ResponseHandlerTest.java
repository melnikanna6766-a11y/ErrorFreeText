package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.ResponseHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseHandlerTest {

    @Test
    public void createCheckTextRequestTest() {
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getInputText()).thenReturn("meow");
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        List<CheckTextsRequest> json = new ResponseHandler().createCheckTextRequest(task, 10_000_000);
        String[] expected = new String[] {"meow"};
        assertEquals(expected[0], json.getFirst().text()[0]);
    }

    @Test
    public void createCheckTextRequestWithUrlTest() {
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getInputText()).thenReturn("https://yandex.ru/dev/speller/doc/ru/reference/checkTexts yandex56");
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        List<CheckTextsRequest> json = new ResponseHandler().createCheckTextRequest(task, 10_000_000);
        assertEquals("en", json.getFirst().lang());
        assertEquals(6, json.getFirst().option());
    }
}
