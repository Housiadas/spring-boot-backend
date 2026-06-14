package com.housi.backend.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import java.io.Serial;

public class ConflictException extends RootException {

    @Serial private static final long serialVersionUID = 1L;

    public ConflictException(ProblemType problemType, String message) {
        super(CONFLICT, problemType, message);
    }
}
