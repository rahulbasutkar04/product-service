package com.project.store.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author rahul
 * Custom Exception
 */
@Setter
@Getter
@NoArgsConstructor
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = - 1261602175228181834L;

    private ErrorResponseEnum errorResponse;

    private Integer statusCode;

    public ApplicationException(ErrorResponseEnum errorResponse) {

        super(errorResponse.getErrorText());
        this.errorResponse = errorResponse;
    }

    public ApplicationException(String errorMessage) {

        super(errorMessage);
    }

    public ApplicationException(ErrorResponseEnum errorResponse, Throwable throwable) {

        super(throwable);
        this.errorResponse = errorResponse;
    }

    // Only used for WebClient errors
    public ApplicationException(ErrorResponseEnum errorResponse, Integer statusCode, Throwable throwable) {

        super(throwable);
        this.errorResponse = errorResponse;
        this.statusCode = statusCode;
    }

    public static long getSerialversionuid() {

        return serialVersionUID;
    }

}
