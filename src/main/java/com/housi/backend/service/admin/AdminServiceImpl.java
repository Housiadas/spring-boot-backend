package com.housi.backend.service.admin;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.response.v1.UserResponse;

@Service
public class AdminServiceImpl implements AdminService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse promoteToAdmin(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || hasAdminRole(userOpt.get())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User does not exist or already an admin");
        }
        Role admin =
                roleRepository
                        .findByName(ROLE_ADMIN)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "ROLE_ADMIN missing"));
        User user = userOpt.get();
        user.getRoles().add(admin);
        return convertToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteNonAdminUser(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || hasAdminRole(userOpt.get())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User does not exist or is an admin");
        }
        userRepository.delete(userOpt.get());
    }

    private boolean hasAdminRole(User user) {
        return user.getRoles().stream().anyMatch(r -> ROLE_ADMIN.equals(r.getName()));
    }

    private UserResponse convertToUserResponse(User user) {
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        Set<String> permissions =
                user.getRoles().stream()
                        .flatMap(r -> r.getPermissions().stream())
                        .map(p -> p.getName())
                        .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                roles,
                permissions);
    }
}
