package com.github.melnikanna6766a11y.errorfreetext.dto;

import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

public record ErrorResponse (
        String errorMessage,
        HttpStatusCode errorCode,
        LocalDateTime timestamp,
        String path) {
}
