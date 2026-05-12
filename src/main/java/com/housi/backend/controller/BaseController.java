package com.housi.backend.controller;

import com.housi.backend.response.ErrorResponse;
import com.housi.backend.response.ResponseBuilder;
import com.housi.backend.response.SuccessResponse;
import java.util.List;

public abstract class BaseController {

    protected <T> SuccessResponse<T> respond() {
        return ResponseBuilder.build();
    }

    public <T> SuccessResponse<T> respond(List<T> items) {
        return ResponseBuilder.build(items);
    }

    public <T> SuccessResponse<T> respond(List<T> items, int page, int size, Long totalSize) {
        return ResponseBuilder.build(items, page, size, totalSize);
    }

    protected ErrorResponse respond(ErrorResponse errorResponse) {
        return ResponseBuilder.build(errorResponse);
    }
}
