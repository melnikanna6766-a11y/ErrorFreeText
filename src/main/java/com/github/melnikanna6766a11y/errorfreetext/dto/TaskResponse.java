package com.github.melnikanna6766a11y.errorfreetext.dto;

import com.github.melnikanna6766a11y.errorfreetext.entity.Status;

import java.util.List;

public record TaskResponse(List<List<SpellerResponse>> responses, Status status, ErrorResponse errorResponse) {
}
