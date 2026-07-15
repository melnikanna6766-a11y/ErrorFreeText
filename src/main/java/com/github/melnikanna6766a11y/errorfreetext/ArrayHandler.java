package com.github.melnikanna6766a11y.errorfreetext;

import java.util.Arrays;

public class ArrayHandler {

    public String[] createResponseArray(String[] text, int from, int limit) {
        if ((from + (limit-from)) < text.length) {
            return Arrays.stream(text).skip(from).limit(limit).toArray(String[]::new);
        } else if (from < text.length - 1){
            return Arrays.stream(text).skip(from).limit(text.length-1).toArray(String[]::new);
        } else if (from == text.length - 1){
            return text;
        }
        return null;
    }

    public int calculateLimit(String[] text) {
        int limit = 0;
        for (String word: text) {
            if (limit + word.length() < 10000) {
                limit += word.length();
            } else {
                return limit;
            }
        }
        return limit;
    }
}
