package com.housi.backend.domain.exception;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class RootException extends RuntimeException {

    @Serial private static final long serialVersionUID = 6378336966214073013L;

    private final HttpStatus httpStatus;
    private final ProblemType problemType;
    private final List<ApiErrorDetails> errors = new ArrayList<>();

    public RootException(
            @NonNull HttpStatus httpStatus,
            @NonNull ProblemType problemType,
            String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.problemType = problemType;
    }
}
