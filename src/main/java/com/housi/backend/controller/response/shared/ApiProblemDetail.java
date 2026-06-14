package com.housi.backend.controller.response.shared;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RFC 7807 Problem Detail error response")
public record ApiProblemDetail(
        @Schema(description = "type that identifies the problem type", example = "user:not-found")
        URI type,
        @Schema(description = "Short summary of the problem type", example = "Bad Request")
        String title,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Human-readable explanation of the problem", example = "Validation failed.")
        String detail,
        @Schema(description = "URI reference identifying this specific occurrence", example = "/api/v1/users")
        URI instance,
        @Schema(description = "Field-level validation errors; only present on 400 responses")
        List<ApiErrorDetails> errors,
        @Schema(description = "Time the error occurred", example = "2024-01-01T00:00:00Z")
        Instant timestamp) {}
