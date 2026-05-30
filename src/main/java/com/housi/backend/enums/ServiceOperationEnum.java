package com.housi.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ServiceOperationEnum {
    CREATING("creating"),
    UPDATING("updating"),
    DELETING("deleting");

    private final String name;
}