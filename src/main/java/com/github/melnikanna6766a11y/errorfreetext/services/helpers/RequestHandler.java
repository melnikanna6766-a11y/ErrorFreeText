package com.github.melnikanna6766a11y.errorfreetext.services.helpers;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@AllArgsConstructor
@Component
public class RequestHandler {
    private LimitsHandler limitsHandler;

    public CheckTextsRequest generateSpellerRequest(Task task) {
        log.info("Creating check text request for task with id {}", task.getId());
        String[] splitedText = task.getInputText().split("[,.\\s]+");
        int maxIndex = calculateAvailableRange(
                Arrays.copyOfRange(splitedText, task.getLastProcessedWordIndex(), splitedText.length),
                limitsHandler.getRequestLimit(),
                limitsHandler.calculateRemainingChars()
        );
        String[] requestArray = Arrays.copyOfRange(
                splitedText,
                task.getLastProcessedWordIndex(),
                maxIndex + task.getLastProcessedWordIndex()
        );
        task.setLastProcessedWordIndex(maxIndex + 1);
        return createCheckTextRequest(task, requestArray, maxIndex);
    }

    private CheckTextsRequest createCheckTextRequest(Task task, String[] textRequest, int charsNumber){
        return new CheckTextsRequest(
                textRequest,
                task.getLanguage().getLanguage(),
                new OptionsHandler().checkOptions(textRequest),
                charsNumber
        );
    }

    private int calculateAvailableRange(String[] splitedText, int requestLimit, long remainingChars) {
        int charCount = 0;
        int maxIndex = 0;
        for (String word: splitedText) {
            if (charCount + word.length() <= requestLimit && charCount + word.length() <= remainingChars) {
                charCount += word.length();
                maxIndex += 1;
            } else {
                return maxIndex;
            }
        }
        return maxIndex;
    }
}
