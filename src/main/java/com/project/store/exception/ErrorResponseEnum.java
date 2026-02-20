package com.project.store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResponseEnum {
    GENERAL_ERROR(100, "An exception has occurred while processing your request.", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(101, "There was one or more validation error(s)", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND(102, "The requested entity could not be found", HttpStatus.BAD_REQUEST),
    USER_CONFLICT(103, "User already exists", HttpStatus.CONFLICT),
    CONFLICT(104, "conflict occurred", HttpStatus.CONFLICT),
    ACCESS_DENIED(105, "Access Denied", HttpStatus.FORBIDDEN),
    UNPROCESSABLE_ENTITY(106, "UNPROCESSABLE ENTITY", HttpStatus.UNPROCESSABLE_ENTITY),
    SERVICE_UNAVAILABLE(107, "Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    AUTHENTICATION_FAILED(108, "Authorization Failed!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_SERVER_ACCESS(109, "Unauthorized Server Access!", HttpStatus.FORBIDDEN),
    INVALID_REQUEST(110, "Invalid request!", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(111, "UnAuthorized Access", HttpStatus.UNAUTHORIZED),
    DUPLICATE_REQUEST(112, "Duplicate Request!", HttpStatus.CONFLICT);

    private final int code;

    private final String errorText;

    private final HttpStatus httpStatus;
}
