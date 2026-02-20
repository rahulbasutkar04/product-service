package com.project.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity <Object> handleValidationException(ValidationException ex) {

        HttpStatus status = ex.getErrorResponse().getHttpStatus();

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getValidationError().getErrorMessage(),
                status.value()
        );

        return new ResponseEntity <>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        ErrorResponse errorResponse = new ErrorResponse(message, 400);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}