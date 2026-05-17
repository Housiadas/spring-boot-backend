package com.housi.backend.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponses> handleResponseStatus(ResponseStatusException exc) {
        HttpStatus status = HttpStatus.valueOf(exc.getStatusCode().value());
        String message = exc.getReason() != null ? exc.getReason() : exc.getMessage();
        return buildResponseEntity(message, status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponses> handleAuth(AuthenticationException exc) {
        return buildResponseEntity("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponses> handleValidation(
            MethodArgumentNotValidException exc) {
        String message =
                exc.getBindingResult().getFieldErrors().stream()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .collect(Collectors.joining("; "));
        return buildResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponses> handleException(Exception exc) {
        return buildResponseEntity(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ExceptionResponses> buildResponseEntity(
            String message, HttpStatus status) {
        ExceptionResponses error = new ExceptionResponses();
        error.setStatus(status.value());
        error.setMessage(message);
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, status);
    }
}
