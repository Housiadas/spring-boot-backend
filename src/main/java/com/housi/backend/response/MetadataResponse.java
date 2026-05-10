package com.housi.backend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetadataResponse {
    private Integer page;
    private Integer size;
    private Long totalSize;
}
