package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckTextsResponseHandlerTest {

    @Test
    public void createCheckTextResponseTest() {
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getInputText()).thenReturn("meow");
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        List<CheckTextsResponse> json = new CheckTextsResponseHandler().createCheckTextResponse(task);
        String expected = "{\"text\":[\"meow\"],\"lang\":\"en\",\"option\":0}";
        assertEquals(expected, json.getFirst());
    }

    public void createCheckTextResponseWithUrlTest() {
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getInputText()).thenReturn("https://yandex.ru/dev/speller/doc/ru/reference/checkTexts yandex");
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        List<CheckTextsResponse> json = new CheckTextsResponseHandler().createCheckTextResponse(task);
        String expected = "{\"text\":[\"yandex\"],\"lang\":\"en\",\"option\":6}";
        assertEquals(expected, json.getFirst());
    }
}
