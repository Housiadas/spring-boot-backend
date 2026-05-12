package com.housi.backend.response;

import java.util.List;
import lombok.Getter;

@Getter
public class SuccessResponse<T> {

    private List<T> data = List.of();
    private MetadataResponse metadata;

    public SuccessResponse() {}

    public SuccessResponse(List<T> data) {
        this.data = data;
    }

    public SuccessResponse(List<T> data, Integer page, Integer size, Long totalSize) {
        this.data = data;
        this.metadata = new MetadataResponse(page, size, totalSize);
    }
}
