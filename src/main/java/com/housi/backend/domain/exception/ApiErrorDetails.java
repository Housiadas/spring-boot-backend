package com.housi.backend.domain.exception;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

@Builder
public record ApiErrorDetails(@JsonInclude(Include.NON_NULL) String pointer, String reason)
        implements Serializable {}
