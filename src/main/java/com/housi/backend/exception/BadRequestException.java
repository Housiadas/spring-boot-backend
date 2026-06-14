package com.housi.backend.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.Serial;

public class BadRequestException extends RootException {

    @Serial private static final long serialVersionUID = 1L;

    public BadRequestException(ProblemType problemType, String message) {
        super(BAD_REQUEST, problemType, message);
    }
}
