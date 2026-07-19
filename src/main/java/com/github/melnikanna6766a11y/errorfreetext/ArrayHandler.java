package com.github.melnikanna6766a11y.errorfreetext;

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
        if (text == null || from < 0 || limit < 0) {
            return null;
        }
        int lim = Math.min(from+limit, text.length);
        log.info("A limit in the amount of {} has been calculated", lim);
        return Arrays.copyOfRange(text, from, lim);
    }

    public int calculateLimit(String[] text) {
        int charCount = 0;
        int limit = 0;
        for (String word: text) {
            if (charCount + word.length() <= 10000) {
                charCount += word.length();
                limit += 1;
            } else {
                return limit;
            }
        }
        return limit;
    }
}
