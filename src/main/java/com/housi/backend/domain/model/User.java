package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements Serializable {

    @Serial private static final long serialVersionUID = 2134607105408362080L;

    @EqualsAndHashCode.Include
    UUID id;
    String firstName;
    String lastName;
    String email;
    String password;
    String createdBy;
    String updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Set<Role> roles;

    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(
                        roles.stream(), roles.stream().flatMap(r -> r.getPermissions().stream()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public String getUsername() {
        return email;
    }
}
