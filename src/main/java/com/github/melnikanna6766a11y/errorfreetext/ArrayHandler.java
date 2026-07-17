package com.github.melnikanna6766a11y.errorfreetext;

import java.util.Arrays;

public class ArrayHandler {

    public String[] createResponseArray(String[] text, int from, int limit) {
        if (text == null || from < 0 || limit < 0) {
            return null;
        }
        int lim = Math.min(from+limit, text.length);
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
