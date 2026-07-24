package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.services.helpers.SpellerRequestSender;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpellerRequestSenderTest {

    @Test
    public void sendRequest() {
        SpellerRequestSender spellerRequestSender = new SpellerRequestSender();
        CheckTextsRequest checkTextsRequest = new CheckTextsRequest(new String[]{"карова", "мло", "мяумяу"}, "ru", 0);
        List<List<SpellerResponse>> correctedTextResponse = spellerRequestSender.sendRequest(checkTextsRequest);
        assertEquals("карова" ,correctedTextResponse.getFirst().getFirst().word());
    }
}
