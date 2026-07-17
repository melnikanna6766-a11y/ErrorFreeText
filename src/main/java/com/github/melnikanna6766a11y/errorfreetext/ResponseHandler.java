package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsResponse;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class ResponseHandler {

    public List<CheckTextsResponse> createCheckTextResponse(Task task) {
        int index = 0;
        String[] text = task.getInputText().split(" ");
        ArrayHandler arrayHandler = new ArrayHandler();
        int limit = arrayHandler.calculateLimit(text);
        List<CheckTextsResponse> responses = new ArrayList<>();
        while (index <= text.length-1) {
            String[] textResponse = arrayHandler.createResponseArray(text, index, limit);
            index += limit + 1;
            CheckTextsResponse checkTextsResponse = new CheckTextsResponse(
                    textResponse,
                    task.getLanguage().getLanguage(),
                    new OptionsHandler().checkOptions(text));
            responses.add(checkTextsResponse);
        }
        return responses;
    }

    public boolean createCorrectedTextResponse(Task task) {
        RequestSender requestSender = new RequestSender();
        List<CheckTextsResponse> responses = createCheckTextResponse(task);
        List<CorrectedTextResponse> correctedTextResponses = new ArrayList<>();
        for (CheckTextsResponse response : responses) {
            List<List<CorrectedTextResponse>> correctedTextResponse;
            if ((correctedTextResponse = requestSender.sendRequest(response)) != null) {
                for (List<CorrectedTextResponse> correctedTextResponseList: correctedTextResponse) {
                    correctedTextResponses.addAll(correctedTextResponseList);
                }
            } else {
                return false;
            }
        }
        task.setResponse(correctedTextResponses);
        return true;
    }
}
