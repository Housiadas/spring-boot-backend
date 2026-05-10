package com.housi.backend.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private T data;
    private ErrorResponse errors;

    public ApiResponse() {}

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(ErrorResponse errors) {
        this.errors = errors;
    }
}
