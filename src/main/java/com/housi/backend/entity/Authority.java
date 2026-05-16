package com.housi.backend.entity;

import jakarta.persistence.Embeddable;

import org.springframework.security.core.GrantedAuthority;

import lombok.RequiredArgsConstructor;

@Embeddable
@RequiredArgsConstructor
public class Authority implements GrantedAuthority {

    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
