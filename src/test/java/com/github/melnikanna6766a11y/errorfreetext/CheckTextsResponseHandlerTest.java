package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.entity.Language;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class CheckTextsResponseHandlerTest {

    @Test
    public void createCheckTextResponseTest() {
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getText()).thenReturn("meow");
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        List<String> json = new CheckTextsResponseHandler().createCheckTextResponse(task);
        String expected = "{\"text\":[\"meow\"],\"lang\":\"en\",\"option\":0}";
        Assert.assertEquals(expected, json.getFirst());
    }

    public void createCheckTextResponseWithUrlTest() {
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getText()).thenReturn("https://yandex.ru/dev/speller/doc/ru/reference/checkTexts yandex");
        Language language = Mockito.mock(Language.class);
        Mockito.when(language.getLanguage()).thenReturn("en");
        Mockito.when(task.getLanguage()).thenReturn(language);
        List<String> json = new CheckTextsResponseHandler().createCheckTextResponse(task);
        String expected = "{\"text\":[\"yandex\"],\"lang\":\"en\",\"option\":6}";
        Assert.assertEquals(expected, json.getFirst());
    }
}
