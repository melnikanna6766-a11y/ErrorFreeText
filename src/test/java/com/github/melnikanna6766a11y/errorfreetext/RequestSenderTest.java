package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsResponse;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestSenderTest {

    @Test
    public void sendRequest() {
        RequestSender requestSender = new RequestSender();
        CheckTextsResponse checkTextsResponse = new CheckTextsResponse(new String[]{"карова", "мло", "мяумяу"}, "ru", 0);
        List<List<CorrectedTextResponse>> correctedTextResponse = requestSender.sendRequest(checkTextsResponse);
        assertEquals("карова" ,correctedTextResponse.getFirst().getFirst().word());
    }
}
