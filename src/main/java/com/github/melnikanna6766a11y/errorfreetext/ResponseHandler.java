package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ResponseHandler {

    public List<CheckTextsRequest> createCheckTextRequest(Task task) {
        log.info("Creating check text request for task with id {}", task.getId());
        int index = 0;
        String[] text = task.getInputText().split(" ");
        ArrayHandler arrayHandler = new ArrayHandler();
        int limit = arrayHandler.calculateLimit(text);
        List<CheckTextsRequest> requests = new ArrayList<>();
        while (index <= text.length-1) {
            String[] textResponse = arrayHandler.createResponseArray(text, index, limit);
            index += limit + 1;
            CheckTextsRequest checkTextsRequest = new CheckTextsRequest(
                    textResponse,
                    task.getLanguage().getLanguage(),
                    new OptionsHandler().checkOptions(text));
            requests.add(checkTextsRequest);
        }
        log.info("Created check text request for task with id {}, count of requests {}", task.getId(), requests.size());
        return requests;
    }

    public boolean createCorrectedTextResponse(Task task, RequestSender requestSender) {
        log.info("Creating response from Yandex API for task with id {}", task.getId());
        List<CheckTextsRequest> responses = createCheckTextRequest(task);
        List<CorrectedTextResponse> correctedTextResponses = new ArrayList<>();
        for (CheckTextsRequest response : responses) {
            List<List<CorrectedTextResponse>> correctedTextResponse;
            if ((correctedTextResponse = requestSender.sendRequest(response)) != null) {
                for (List<CorrectedTextResponse> correctedTextResponseList: correctedTextResponse) {
                    correctedTextResponses.addAll(correctedTextResponseList);
                }
            } else {
                log.info("Response for task with id {}, was not created because no response body was available", task.getId());
                return false;
            }
        }
        task.setResponse(correctedTextResponses);
        log.info("Response for task with id {}, was created", task.getId());
        return true;
    }
}
