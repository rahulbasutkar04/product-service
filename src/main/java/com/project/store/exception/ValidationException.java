package com.project.store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ValidationException extends ApplicationException{

    private static final long serialVersionUID = 712538634478455493L;

    // To throw single error.
    private ValidationError validationError;

    // To throw multiple errors.
    private List<ValidationError> validationErrorList;

    public ValidationException(List <ValidationError> validationErrorList,
                               ErrorResponseEnum errorResponse) {
        super(errorResponse);
        this.validationErrorList = validationErrorList;
    }

    public ValidationException(List<ValidationError> validationErrorList,
                               ErrorResponseEnum errorResponse,
                               Throwable throwable) {
        super(errorResponse, throwable);
        this.validationErrorList = validationErrorList;
    }

    public ValidationException(ValidationError validationError, ErrorResponseEnum errorResponse) {
        super(errorResponse);
        this.validationError = validationError;
    }

    public ValidationException(ValidationError validationError, ErrorResponseEnum errorResponse,
                               Throwable throwable) {
        super(errorResponse, throwable);
        this.validationError = validationError;
    }
    // Only for WebClient errors
    public ValidationException(ValidationError validationError, ErrorResponseEnum errorResponse,
                               Integer statusCode, Throwable throwable) {
        super(errorResponse, statusCode, throwable);
        this.validationError = validationError;
    }

}
