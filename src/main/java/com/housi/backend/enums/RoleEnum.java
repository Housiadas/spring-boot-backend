package com.housi.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoleEnum {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    // Compile-time constants for use in @Query annotation strings
    public static final String ADMIN_NAME = "ROLE_ADMIN";

    private final String name;
}
