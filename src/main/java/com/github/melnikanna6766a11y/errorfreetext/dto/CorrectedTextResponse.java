package com.github.melnikanna6766a11y.errorfreetext.dto;

public record CorrectedTextResponse(
        int code,
        int pos,
        int row,
        int col,
        int len,
        String word,
        String s) {
}
