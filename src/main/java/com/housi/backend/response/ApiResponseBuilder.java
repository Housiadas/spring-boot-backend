package com.housi.backend.response;

import java.util.List;

public class ApiResponseBuilder {

    private ApiResponseBuilder() {}

    public static <T> ApiResponse<DataResponse<T>> build(List<T> items) {
        return new ApiResponse<>(new DataResponse<>(items));
    }

    public static <T> ApiResponse<DataResponse<T>> build(
            List<T> items, Integer page, Integer size, Long totalSize) {
        return new ApiResponse<>(new DataResponse<>(items, page, size, totalSize));
    }

    public static <T> ApiResponse<T> build(T item) {
        return new ApiResponse<>(item);
    }

    public static ApiResponse<ErrorResponse> build(ErrorResponse errorResponse) {
        return new ApiResponse<>(errorResponse);
    }
}
