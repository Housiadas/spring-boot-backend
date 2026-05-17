package com.housi.backend.request.v1.admin;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignPermissionsRequest {
    private Set<String> permissions;
}
