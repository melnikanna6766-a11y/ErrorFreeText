package com.github.melnikanna6766a11y.errorfreetext.dto;

import java.util.List;

public record SpellerResponse(
        int code,
        int pos,
        int row,
        int col,
        int len,
        String word,
        List<String> s) {
}
