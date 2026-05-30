package com.housi.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EntityTransactionLogEnum {
    CREATE("created"),
    UPDATE("updated"),
    DELETE("deleted"),
    UNKNOWN("unknown");

    private final String name;
}
