package com.housi.backend.response;

import java.util.List;

public class ResponseBuilder {

    private ResponseBuilder() {}

    public static <T> SuccessResponse<T> build() {
        return new SuccessResponse<>();
    }

    public static <T> SuccessResponse<T> build(List<T> items) {
        return new SuccessResponse<>(items);
    }

    public static <T> SuccessResponse<T> build(
            List<T> items, Integer page, Integer size, Long totalSize) {
        return new SuccessResponse<>(items, page, size, totalSize);
    }

    public static ErrorResponse build(ErrorResponse errorResponse) {
        return errorResponse;
    }
}
