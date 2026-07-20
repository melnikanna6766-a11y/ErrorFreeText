package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestSenderTest {

    @Test
    public void sendRequest() {
        RequestSender requestSender = new RequestSender();
        CheckTextsRequest checkTextsRequest = new CheckTextsRequest(new String[]{"карова", "мло", "мяумяу"}, "ru", 0);
        List<List<CorrectedTextResponse>> correctedTextResponse = requestSender.sendRequest(checkTextsRequest);
        assertEquals("карова" ,correctedTextResponse.getFirst().getFirst().word());
    }
}
