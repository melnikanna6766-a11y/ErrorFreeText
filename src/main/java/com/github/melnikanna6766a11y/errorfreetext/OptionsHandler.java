package com.github.melnikanna6766a11y.errorfreetext;

import java.util.Arrays;

public class OptionsHandler {

    public int checkOptions(String[] text) {
        int options = 0;
        if (Arrays.stream(text).anyMatch(word -> {
            return word.matches("\\.\\d");
        })) {
            options+=2;
        }
        if (Arrays.stream(text).anyMatch(word -> {
            return word.matches("^(https?://).*");
        })) {
            options+=4;
        }
        return options;
    }
}
