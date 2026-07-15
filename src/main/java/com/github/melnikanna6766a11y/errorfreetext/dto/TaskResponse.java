package com.github.melnikanna6766a11y.errorfreetext.dto;

import java.util.List;

public record TaskResponse(List<CorrectedTextResponse> responses, String status) {
}
