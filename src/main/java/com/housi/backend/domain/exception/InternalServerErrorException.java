package com.housi.backend.domain.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.Serial;

import com.housi.backend.constant.AppConstants;

public class InternalServerErrorException extends RootException {

    @Serial private static final long serialVersionUID = 694110374288090930L;

    public InternalServerErrorException() {
        super(
                INTERNAL_SERVER_ERROR,
                ProblemType.INTERNAL_ERROR,
                AppConstants.API_DEFAULT_ERROR_MESSAGE);
    }

    public InternalServerErrorException(String message) {
        super(INTERNAL_SERVER_ERROR, ProblemType.INTERNAL_ERROR, message);
    }
}
