package com.housi.backend.usecase.user;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.enums.EntityTransactionAuditEnum;
import com.housi.backend.domain.enums.RoleEnum;
import com.housi.backend.domain.event.EntityAuditEvent;
import com.housi.backend.domain.exception.BadRequestException;
import com.housi.backend.domain.exception.ConflictException;
import com.housi.backend.domain.exception.InternalServerErrorException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.exception.ResourceNotFoundException;
import com.housi.backend.domain.model.Role;
import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.RolePort;
import com.housi.backend.domain.port.out.UserPort;
import com.housi.backend.infrastructure.audit.AuditLogger;

@Service
@Transactional(readOnly = true)
public class UserAdminUseCase {

    private final UserPort userPort;
    private final RolePort rolePort;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;
    private final LastAdminGuard lastAdminGuard;

    public UserAdminUseCase(
            UserPort userPort,
            RolePort rolePort,
            PasswordEncoder passwordEncoder,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher,
            LastAdminGuard lastAdminGuard) {
        this.userPort = userPort;
        this.rolePort = rolePort;
        this.passwordEncoder = passwordEncoder;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
        this.lastAdminGuard = lastAdminGuard;
    }

    public List<User> getAll() {
        return userPort.findAllWithRolesAndPermissions();
    }

    public User getById(UUID id) {
        return requireUser(id);
    }

    @Transactional
    public User create(String firstName, String lastName, String email, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BadRequestException(ProblemType.PASSWORD_REQUIRED, "Password is required.");
        }
        if (userPort.existsByEmail(email)) {
            throw new ConflictException(ProblemType.DUPLICATE_EMAIL, "Email already taken.");
        }
        User user =
                userPort.save(
                        User.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .email(email)
                                .password(passwordEncoder.encode(rawPassword))
                                .roles(Set.of(requireRole(RoleEnum.USER.getName())))
                                .build());
        auditLogger.userAdminCreated(user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        user.getId(), "User", user.getEmail(), EntityTransactionAuditEnum.CREATE));
        return requireUser(user.getId());
    }

    @Transactional
    public User update(UUID id, String firstName, String lastName, String email) {
        User user = requireUser(id);
        if (!user.getEmail().equals(email) && userPort.existsByEmail(email)) {
            throw new ConflictException(ProblemType.DUPLICATE_EMAIL, "Email already taken.");
        }
        userPort.save(user.toBuilder().firstName(firstName).lastName(lastName).email(email).build());
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
        userPort.delete(user);
    }

    private User requireUser(UUID id) {
        return userPort.findByIdWithRolesAndPermissions(id)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        ProblemType.USER_NOT_FOUND,
                                        "User with id '" + id + "' not found."));
    }

    private Role requireRole(String name) {
        return rolePort.findByName(name)
                .orElseThrow(
                        () -> new InternalServerErrorException("Required role missing: " + name));
    }
}
