package com.housi.backend.domain.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.Serial;

public class ResourceNotFoundException extends RootException {

    @Serial private static final long serialVersionUID = 26377136569699646L;

    public ResourceNotFoundException(ProblemType problemType, String message) {
        super(NOT_FOUND, problemType, message);
    }
}
