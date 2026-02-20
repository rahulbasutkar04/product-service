package com.project.store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResponseEnum {
    GENERAL_ERROR(100, "An exception has occurred while processing your request.", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(101, "There was one or more validation error(s)", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND(103, "The requested entity could not be found", HttpStatus.BAD_REQUEST),
    USER_CONFLICT(112, "User already exists", HttpStatus.CONFLICT),
    CONFLICT(113, "conflict occurred", HttpStatus.CONFLICT),
    ACCESS_DENIED(115, "Access Denied", HttpStatus.FORBIDDEN),
    DATA_MISSING(116, "Some Inputs are missing", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(120, "Wrong Credentials", HttpStatus.BAD_REQUEST),
    NOT_IMPLEMENTED(118, "NOT IMPLEMENTED", HttpStatus.NOT_IMPLEMENTED),
    UNPROCESSABLE_ENTITY(119, "UNPROCESSABLE ENTITY", HttpStatus.UNPROCESSABLE_ENTITY),
    SERVICE_UNAVAILABLE(121, "Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    AUTHENTICATION_FAILED(130, "Authorization Failed!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_SERVER_ACCESS(131, "Unauthorized Server Access!", HttpStatus.FORBIDDEN),
    INVALID_REQUEST(135, "Invalid request!", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(136,"UnAuthorized Access",HttpStatus.UNAUTHORIZED ),
    DUPLICATE_REQUEST(137,"Duplicate Request!", HttpStatus.CONFLICT );


    private final int code;

    private final String errorText;

    private final HttpStatus httpStatus;
}
