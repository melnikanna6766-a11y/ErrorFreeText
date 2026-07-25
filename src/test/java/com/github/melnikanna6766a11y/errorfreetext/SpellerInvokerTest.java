package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.SpellerInvoker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SpellerInvokerTest {
    @Mock
    Task task;

    @Test
    public void sendRequestToSpeller() {
        SpellerInvoker spellerInvoker = new SpellerInvoker();
        CheckTextsRequest checkTextsRequest = new CheckTextsRequest(new String[]{"карова", "мло", "мяумяу"}, "ru", 0, 15);
        List<List<SpellerResponse>> correctedTextResponse = spellerInvoker.sendRequestToSpeller(checkTextsRequest, task);
        assertEquals("карова" ,correctedTextResponse.getFirst().getFirst().word());
        assertEquals(3, correctedTextResponse.size());
        assertEquals(1, correctedTextResponse.getFirst().size());
    }

    @Test
    public void composeResponseFromSpellerTest() {
        SpellerInvoker spellerInvoker = new SpellerInvoker();
        List<List<SpellerResponse>> correctedTextResponse = new ArrayList<>();
        List<SpellerResponse> innerResponseList = new ArrayList<>();
        List<SpellerResponse> innerResponseList2 = new ArrayList<>();
        SpellerResponse spellerResponse = Mockito.mock(SpellerResponse.class);
        SpellerResponse spellerResponse2 = Mockito.mock(SpellerResponse.class);
        innerResponseList.add(spellerResponse);
        innerResponseList2.add(spellerResponse2);
        correctedTextResponse.add(innerResponseList2);
        correctedTextResponse.add(innerResponseList);
        List<List<SpellerResponse>> spellerComposeResponse =  spellerInvoker.composeResponseFromSpeller(correctedTextResponse);
        assertEquals(2, spellerComposeResponse.getFirst().size());
    }
}
