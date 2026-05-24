package com.housi.backend.exception;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

import com.housi.backend.response.shared.ApiErrorDetails;

import lombok.Getter;

@Getter
public class RootException extends RuntimeException {

    @Serial private static final long serialVersionUID = 6378336966214073013L;

    private final HttpStatus httpStatus;
    private final List<ApiErrorDetails> errors = new ArrayList<>();

    public RootException(@NonNull final HttpStatus httpStatus) {
        super();
        this.httpStatus = httpStatus;
    }

    public RootException(@NonNull final HttpStatus httpStatus, final String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
