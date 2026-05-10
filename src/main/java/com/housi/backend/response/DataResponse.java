package com.housi.backend.response;

import java.util.List;
import lombok.Getter;

@Getter
public class DataResponse<T> {

    private List<T> data = List.of();
    private MetadataResponse metadata;

    public DataResponse() {}

    public DataResponse(List<T> data) {
        this.data = data;
    }

    public DataResponse(List<T> data, Integer page, Integer size, Long totalSize) {
        this.data = data;
        this.metadata = new MetadataResponse(page, size, totalSize);
    }
}
