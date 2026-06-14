package com.housi.backend.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.Serial;

public class NotAuthorizedException extends RootException {

    @Serial private static final long serialVersionUID = -711441617476620028L;

    public NotAuthorizedException(ProblemType problemType, String message) {
        super(UNAUTHORIZED, problemType, message);
    }
}
