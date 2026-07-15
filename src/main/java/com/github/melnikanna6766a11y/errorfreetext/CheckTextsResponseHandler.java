package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class CheckTextsResponseHandler {

    public List<CheckTextsResponse> createCheckTextResponse(Task task) {
        int index = 0;
        String[] text = task.getInputText().split(" ");
        ArrayHandler arrayHandler = new ArrayHandler();
        int limit = arrayHandler.calculateLimit(text);
        List<CheckTextsResponse> responses = new ArrayList<>();
        while (index <= text.length-1) {
            String[] textResponse = arrayHandler.createResponseArray(text, index, limit);
            index += limit;
            CheckTextsResponse checkTextsResponse = new CheckTextsResponse(
                    textResponse,
                    task.getLanguage().getLanguage(),
                    new OptionsHandler().checkOptions(text));
            responses.add(checkTextsResponse);
        }
        return responses;
    }
}
