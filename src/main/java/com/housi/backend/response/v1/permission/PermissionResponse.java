package com.housi.backend.response.v1.permission;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PermissionResponse {
    private UUID id;
    private String name;
    private String description;
}
