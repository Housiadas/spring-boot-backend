package com.housi.backend.controller;

import com.housi.backend.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController {

    private final List<User> users = new ArrayList<>();

    public UserController() {
        initializeUsers();
    }

    @GetMapping("/")
    public List<User> getUsers() {
        return users;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable UUID id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    private void initializeUsers() {
        users.addAll(
                List.of(
                        new User(UUID.randomUUID(), "chris", "housi1", "housi1@housi.com", "1224t23"),
                        new User(UUID.randomUUID(), "chris", "housi2", "housi2@housi.com", "1224t23"),
                        new User(UUID.randomUUID(), "chris", "housi3", "housi3@housi.com", "1224t23"),
                        new User(UUID.randomUUID(), "chris", "housi4", "housi4@housi.com", "1224t23")));
    }
}
