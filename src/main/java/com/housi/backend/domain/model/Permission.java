package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permission implements GrantedAuthority, Serializable {

    @Serial private static final long serialVersionUID = 2137607105408362080L;

    private UUID id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public Permission() {}

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
