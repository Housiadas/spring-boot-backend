package com.housi.backend.controller;

import com.housi.backend.entity.User;
import com.housi.backend.request.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    private final List<User> users = new ArrayList<>();

    public UserController() {
        initializeUsers();
    }

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<User> getUsers(
            @Parameter(description = "Optional query parameter") @RequestParam(required = false)
                    String email) {
        if (email == null) {
            return users;
        }
        return users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).toList();
    }

    @Operation(summary = "Create a new user", description = "Add a new user to the list")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createBook(@Valid @RequestBody UserRequest userRequest) {
        User user = convertToUser(UUID.randomUUID(), userRequest);

        users.add(user);
    }

    @Operation(summary = "Get a user by Id", description = "Retrieve a specific user by Id")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    private User convertToUser(UUID id, UserRequest userRequest) {
        return new User(
                id,
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getEmail(),
                userRequest.getPassword());
    }

    private void initializeUsers() {
        users.addAll(
                List.of(
                        new User(
                                UUID.randomUUID(),
                                "chris",
                                "housi1",
                                "housi1@housi.com",
                                "1224t23"),
                        new User(
                                UUID.randomUUID(),
                                "chris",
                                "housi2",
                                "housi2@housi.com",
                                "1224t23"),
                        new User(
                                UUID.randomUUID(),
                                "chris",
                                "housi3",
                                "housi3@housi.com",
                                "1224t23"),
                        new User(
                                UUID.randomUUID(),
                                "chris",
                                "housi4",
                                "housi4@housi.com",
                                "1224t23")));
    }
}
