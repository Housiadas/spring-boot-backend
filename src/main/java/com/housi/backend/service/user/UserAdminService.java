package com.housi.backend.service.user;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;
import com.housi.backend.enums.EntityTransactionAuditEnum;
import com.housi.backend.enums.RoleEnum;
import com.housi.backend.event.EntityAuditEvent;
import com.housi.backend.exception.BadRequestException;
import com.housi.backend.exception.ConflictException;
import com.housi.backend.exception.InternalServerErrorException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.exception.ResourceNotFoundException;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.service.audit.AuditLogger;

@Service
@Transactional(readOnly = true)
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;
    private final LastAdminGuard lastAdminGuard;

    public UserAdminService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher,
            LastAdminGuard lastAdminGuard) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
        this.lastAdminGuard = lastAdminGuard;
    }

    public List<User> getAll() {
        return userRepository.findAllWithRolesAndPermissions();
    }

    public User getById(UUID id) {
        return requireUser(id);
    }

    @Transactional
    public User create(String firstName, String lastName, String email, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BadRequestException(ProblemType.PASSWORD_REQUIRED, "Password is required.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(ProblemType.DUPLICATE_EMAIL, "Email already taken.");
        }
        User user =
                new User(
                        firstName,
                        lastName,
                        email,
                        passwordEncoder.encode(rawPassword),
                        Set.of(requireRole(RoleEnum.USER.getName())));
        userRepository.save(user);
        auditLogger.userAdminCreated(user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        user.getId(), "User", user.getEmail(), EntityTransactionAuditEnum.CREATE));
        return requireUser(user.getId());
    }

    @Transactional
    public User update(UUID id, String firstName, String lastName, String email) {
        User user = requireUser(id);
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ConflictException(ProblemType.DUPLICATE_EMAIL, "Email already taken.");
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        userRepository.save(user);
        auditLogger.userAdminUpdated(user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        user.getId(), "User", user.getEmail(), EntityTransactionAuditEnum.UPDATE));
        return requireUser(id);
    }

    @Transactional
    public void delete(UUID id) {
        User user = requireUser(id);
        lastAdminGuard.assertCanDelete(user);
        auditLogger.userAdminDeleted(user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        user.getId(), "User", user.getEmail(), EntityTransactionAuditEnum.DELETE));
        userRepository.delete(user);
    }

    private User requireUser(UUID id) {
        return userRepository
                .findByIdWithRolesAndPermissions(id)
                .orElseThrow(() -> new ResourceNotFoundException(ProblemType.USER_NOT_FOUND, "User with id '" + id + "' not found."));
    }

    private Role requireRole(String name) {
        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new InternalServerErrorException("Required role missing: " + name));
    }
}
