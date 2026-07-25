package com.github.melnikanna6766a11y.errorfreetext.dto;

import com.github.melnikanna6766a11y.errorfreetext.validate.ValidateLang;
import com.github.melnikanna6766a11y.errorfreetext.validate.ValidateRequestText;

public record TaskRequest(
        @ValidateRequestText String text,
        @ValidateLang String lang) {
}
