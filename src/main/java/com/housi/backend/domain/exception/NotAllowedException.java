package com.housi.backend.domain.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.Serial;

public class NotAllowedException extends RootException {

    @Serial private static final long serialVersionUID = 1L;

    public NotAllowedException(ProblemType problemType, String message) {
        super(FORBIDDEN, problemType, message);
    }
}
