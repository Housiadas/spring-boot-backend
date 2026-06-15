package com.housi.backend.domain.exception;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import java.io.Serial;

public class TooManyRequestsException extends RootException {

    @Serial private static final long serialVersionUID = 1L;

    public TooManyRequestsException(String message) {
        super(TOO_MANY_REQUESTS, ProblemType.TOO_MANY_REQUESTS, message);
    }
}
