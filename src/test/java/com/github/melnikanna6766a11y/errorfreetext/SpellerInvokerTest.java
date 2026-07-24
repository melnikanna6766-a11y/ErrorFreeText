package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.SpellerInvoker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpellerInvokerTest {

    @Test
    public void sendRequestToSpeller() {
        SpellerInvoker spellerInvoker = new SpellerInvoker();
        CheckTextsRequest checkTextsRequest = new CheckTextsRequest(new String[]{"карова", "мло", "мяумяу"}, "ru", 0);
        List<List<SpellerResponse>> correctedTextResponse = spellerInvoker.sendRequestToSpeller(checkTextsRequest);
        assertEquals("карова" ,correctedTextResponse.getFirst().getFirst().word());
    }
}
