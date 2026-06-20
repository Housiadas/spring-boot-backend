package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Permission implements GrantedAuthority, Serializable {

    @Serial private static final long serialVersionUID = 2137607105408362080L;

    @EqualsAndHashCode.Include
    UUID id;
    String name;
    String description;
    Date createdAt;
    Date updatedAt;

    @Override
    public String getAuthority() {
        return name;
    }
}
