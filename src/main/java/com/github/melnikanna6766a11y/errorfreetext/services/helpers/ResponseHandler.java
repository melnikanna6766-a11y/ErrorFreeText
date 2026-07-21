package com.github.melnikanna6766a11y.errorfreetext.services.helpers;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Component
public class ResponseHandler {
    @Value("${status.id.completed}")
    private long completed;
    @Value("${status.id.error}")
    private long error;
    @Value("${status.id.incompleted}")
    private long incompleted;


    public List<CheckTextsRequest> createCheckTextRequest(Task task, int remainingChars) {
        log.info("Creating check text request for task with id {}", task.getId());
        int index = task.getNumberOfSavedElements();
        String[] text = task.getInputText().split(" ");
        ArrayHandler arrayHandler = new ArrayHandler();
        int limit = arrayHandler.calculateLimit(text, remainingChars);
        List<CheckTextsRequest> requests = new ArrayList<>();
        AtomicInteger writtenChars = new AtomicInteger();
        while (index < text.length && writtenChars.get() < remainingChars) {
            String[] textResponse = arrayHandler.createResponseArray(text, index, limit);
            CheckTextsRequest checkTextsRequest = new CheckTextsRequest(
                    textResponse,
                    task.getLanguage().getLanguage(),
                    new OptionsHandler().checkOptions(textResponse));
            requests.add(checkTextsRequest);
            Arrays.stream(textResponse).forEach(word -> writtenChars.addAndGet(word.length()));
            index = limit + 1;
            limit = index - 1 + arrayHandler.calculateLimit(Arrays.copyOfRange(text, index, text.length), remainingChars);
        }
        log.info("Created check text request for task with id {}, count of requests {}", task.getId(), requests.size());
        return requests;
    }

    public long createCorrectedTextResponse(Task task, RequestSender requestSender, int remainingChars) {
        log.info("Creating response from Yandex speller API for task with id {}", task.getId());
        int numberOfSavedElements = 0;
        List<CheckTextsRequest> requests = createCheckTextRequest(task, remainingChars);
        List<CorrectedTextResponse> correctedTextResponses = new ArrayList<>();
        for (CheckTextsRequest request : requests) {
            List<List<CorrectedTextResponse>> correctedTextResponse;
            numberOfSavedElements += request.text().length;
            if ((correctedTextResponse = requestSender.sendRequest(request)) != null) {
                for (List<CorrectedTextResponse> correctedTextResponseList: correctedTextResponse) {
                    correctedTextResponses.addAll(correctedTextResponseList);
                }
            } else {
                log.info("Response for task with id {}, was not created because no response body was available", task.getId());
                return error;
            }
        }
        if (task.getResponse() != null) {
            correctedTextResponses.addAll(task.getResponse());
        }
        task.setResponse(correctedTextResponses);
        log.info("Response for task with id {}, was created", task.getId());
        task.setNumberOfSavedElements(numberOfSavedElements);
        if (requests.size() < task.getNumberOfExecutions()) {
            return incompleted;
        } else {
            return completed;
        }
    }
}
