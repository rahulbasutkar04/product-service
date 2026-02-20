package com.project.store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidationErrorType {
    REQUIRED_FIELD_MISSING("REQUIRED_FIELD_MISSING"),
    INVALID_VALUE("INVALID_VALUE"),
    INVALID_REQUEST("INVALID_REQUEST"),
    UNPROCESSABLE("UNPROCESSABLE"),
    UNAUTHORIZED("UNAUTHORIZED");

    private final String errorType;
}
