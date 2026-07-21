package com.github.melnikanna6766a11y.errorfreetext.services.helpers;

import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class ArrayHandler {

    public String[] createResponseArray(String[] text, int from, int limit) {
        log.info(
                "Generating an array for a Yandex API request with text: {}, from: {}, limit: {}",
                Arrays.toString(text),
                from,
                limit
        );
        int lim = Math.min(limit, text.length);
        log.info("A limit in the amount of {} has been calculated", lim);
        return Arrays.copyOfRange(text, from, lim);
    }

    public int calculateLimit(String[] text, int remainingChars) {
        int charCount = 0;
        int limit = 0;
        for (String word: text) {
            if (charCount + word.length() <= 10000 && charCount + word.length() <= remainingChars) {
                charCount += word.length();
                limit += 1;
            } else {
                return limit;
            }
        }
        return limit;
    }
}
