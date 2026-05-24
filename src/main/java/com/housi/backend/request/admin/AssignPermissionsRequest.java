package com.housi.backend.request.admin;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignPermissionsRequest {
    private Set<String> permissions;
}
