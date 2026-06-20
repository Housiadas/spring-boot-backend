package com.housi.backend.infrastructure.web.advice;

import static com.housi.backend.constant.AppConstants.API_DEFAULT_ERROR_MESSAGE;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.sql.BatchUpdateException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LazyInitializationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.jspecify.annotations.NonNull;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.housi.backend.domain.exception.ApiErrorDetails;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.exception.RootException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull final MethodArgumentNotValidException ex,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {
        log.atInfo().setMessage("Method argument validation exception").setCause(ex).log();

        final List<ApiErrorDetails> errors = new ArrayList<>();
        for (final ObjectError err : ex.getBindingResult().getAllErrors()) {
            errors.add(
                    ApiErrorDetails.builder()
                            .pointer(((FieldError) err).getField())
                            .reason(err.getDefaultMessage())
                            .build());
        }

        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        buildProblemDetail(
                                BAD_REQUEST,
                                ProblemType.VALIDATION_FAILED,
                                "Validation failed.",
                                errors));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            @NonNull final HandlerMethodValidationException ex,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {
        log.atInfo().setMessage("Handler method validation exception").setCause(ex).log();

        final List<ApiErrorDetails> errors = new ArrayList<>();
        for (final var validation : ex.getParameterValidationResults()) {
            final String parameterName = validation.getMethodParameter().getParameterName();
            validation
                    .getResolvableErrors()
                    .forEach(
                            error ->
                                    errors.add(
                                            ApiErrorDetails.builder()
                                                    .pointer(parameterName)
                                                    .reason(error.getDefaultMessage())
                                                    .build()));
        }

        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        buildProblemDetail(
                                BAD_REQUEST,
                                ProblemType.VALIDATION_FAILED,
                                "Validation failed.",
                                errors));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ProblemDetail handleJakartaConstraintViolationException(
            final jakarta.validation.ConstraintViolationException ex) {
        log.atInfo().setMessage("Constraint validation exception").setCause(ex).log();

        final List<ApiErrorDetails> errors = new ArrayList<>();
        for (final var violation : ex.getConstraintViolations()) {
            errors.add(
                    ApiErrorDetails.builder()
                            .pointer(
                                    ((PathImpl) violation.getPropertyPath())
                                            .getLeafNode()
                                            .getName())
                            .reason(violation.getMessage())
                            .build());
        }

        return buildProblemDetail(
                BAD_REQUEST, ProblemType.VALIDATION_FAILED, "Validation failed.", errors);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
        org.hibernate.exception.ConstraintViolationException.class,
        DataIntegrityViolationException.class,
        BatchUpdateException.class,
        jakarta.persistence.PersistenceException.class,
    })
    public ProblemDetail handlePersistenceException(final Exception ex) {
        log.atInfo().setMessage("Persistence exception").setCause(ex).log();

        final String cause = NestedExceptionUtils.getMostSpecificCause(ex).getLocalizedMessage();
        final String errorDetail = extractPersistenceDetails(cause);
        return buildProblemDetail(BAD_REQUEST, detectConstraintProblemType(cause), errorDetail);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(final AccessDeniedException ex) {
        log.atInfo().setMessage("Access denied exception").setCause(ex).log();

        return buildProblemDetail(
                HttpStatus.FORBIDDEN, ProblemType.ACCESS_DENIED, "Access denied.");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ProblemDetail handleEmptyResultDataAccessException(
            final EmptyResultDataAccessException ex) {
        log.atInfo().setMessage("Empty result data access exception").setCause(ex).log();

        return buildProblemDetail(
                HttpStatus.NOT_FOUND,
                ProblemType.RESOURCE_NOT_FOUND,
                "No record found for this id.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(LazyInitializationException.class)
    public ProblemDetail handleLazyInitialization(final LazyInitializationException ex) {
        log.atWarn().setMessage("Lazy initialization exception").setCause(ex).log();

        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemType.INTERNAL_ERROR,
                API_DEFAULT_ERROR_MESSAGE);
    }

    @ExceptionHandler(RootException.class)
    public ResponseEntity<ProblemDetail> handleRootException(final RootException ex) {
        log.atInfo().setMessage("Root exception").setCause(ex).log();

        final ProblemDetail problemDetail =
                buildProblemDetail(
                        ex.getHttpStatus(), ex.getProblemType(), ex.getMessage(), ex.getErrors());
        return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ProblemDetail handleAllExceptions(final Throwable ex) {
        log.atWarn().setMessage("Unhandled exception").setCause(ex).log();

        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemType.INTERNAL_ERROR,
                API_DEFAULT_ERROR_MESSAGE);
    }

    private ProblemDetail buildProblemDetail(
            HttpStatus status, ProblemType problemType, String detail) {
        return buildProblemDetail(status, problemType, detail, emptyList());
    }

    private ProblemDetail buildProblemDetail(
            HttpStatus status,
            ProblemType problemType,
            String detail,
            List<ApiErrorDetails> errors) {

        final ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(status, StringUtils.normalizeSpace(detail));
        problemDetail.setType(problemType.getType());
        problemDetail.setTitle(problemType.getTitle());
        problemDetail.setProperty("timestamp", Instant.now());
        if (!CollectionUtils.isEmpty(errors)) {
            problemDetail.setProperty("errors", errors);
        }

        return problemDetail;
    }

    private ProblemType detectConstraintProblemType(final String cause) {
        final String lower = cause.toLowerCase();
        if (lower.contains("slug")) return ProblemType.DUPLICATE_SLUG;
        if (lower.contains("federal_tax_id") || lower.contains("tax"))
            return ProblemType.DUPLICATE_FEDERAL_TAX_ID;
        if (lower.contains("email")) return ProblemType.DUPLICATE_EMAIL;
        return ProblemType.VALIDATION_FAILED;
    }

    private String extractPersistenceDetails(final String cause) {
        String details = API_DEFAULT_ERROR_MESSAGE;

        if (cause.contains("Detail")) {
            final List<String> matchList = new ArrayList<>();
            final Pattern pattern = Pattern.compile("\\((.*?)\\)");
            final Matcher matcher = pattern.matcher(cause);

            while (matcher.find()) {
                matchList.add(matcher.group(1));
            }

            if (matchList.size() == 2) {
                final String key = matchList.get(0);
                final String value = matchList.get(1);
                final String message = cause.substring(cause.lastIndexOf(")") + 1);
                details = format("%s '%s' %s", key, value, message);
            }
        }

        return details;
    }
}
