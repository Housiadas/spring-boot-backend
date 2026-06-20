package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {

    @Serial private static final long serialVersionUID = 2134607105408362080L;

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Role> roles = new HashSet<>();

    public User(String firstName, String lastName, String email, String password, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User() {}

    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(
                        roles.stream(), roles.stream().flatMap(r -> r.getPermissions().stream()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public String getUsername() {
        return email;
    }
}
