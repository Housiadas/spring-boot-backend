package com.housi.backend.response.v1;

import java.util.List;

import com.housi.backend.entity.Authority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

    private long id;

    private String fullName;

    private String email;

    private List<Authority> authorities;
}
