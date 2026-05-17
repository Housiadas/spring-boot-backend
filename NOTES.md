Plan: Concretize the RBAC Feature

Context

The feature/rbac branch lands the structural foundation (User/Role/Permission entities, JWT, @PreAuthorize, Flyway seeds), but several pieces from the reference xkcoding/demo-rbac-security                                    
(https://github.com/xkcoding/spring-boot-demo/tree/master/demo-rbac-security) and basic production-readiness items are missing. Today an admin cannot manage roles or permissions without writing SQL, the JWT secret is a      
placeholder (<<TODO-INSERT-YOUR-SECRET>> in application.properties:30), there is no lockout protection on login, every authority lookup eagerly fetches the entire object graph, and there is zero test coverage for security.  
This milestone closes those gaps so RBAC is actually usable and defensible.

Scope (one milestone)

1. Role & Permission admin CRUD endpoints + user→role assignment.
3. Externalize JWT secret + Redis-backed login attempt lockout.
4. Structured audit logging for auth events (login success/fail, logout, access-denied, role/perm changes).

Out of scope: Redis-backed JWT / logout / online users, refresh tokens, dynamic URL-based authorization. Track separately.

 ---
1. Role & Permission Admin CRUD

New controllers under /api/v1/admin (already protected by ROLE_ADMIN in SecurityConfig.java:68-82), reusing the existing @PreAuthorize("hasAuthority('admin:write|read')") pattern from AdminController.java:30-51.

New files (mirror the structure of service/admin/ and controller/v1/admin/):
- controller/v1/admin/RoleController.java — GET /admin/roles, GET /admin/roles/{id}, POST /admin/roles, PUT /admin/roles/{id}, DELETE /admin/roles/{id}, PUT /admin/roles/{id}/permissions (replace set).
- controller/v1/admin/PermissionController.java — same shape, no permission assignment endpoint.
- controller/v1/admin/UserRoleController.java — PUT /admin/users/{userId}/roles (replace user's role set). Supersedes the narrow AdminController.java:promoteToAdmin.
- service/admin/role/RoleService(+Impl), service/admin/permission/PermissionService(+Impl) — follow the interface/impl split used by LoginService, RegisterService.
- request/v1/admin/RoleRequest, PermissionRequest, AssignRolesRequest, AssignPermissionsRequest — Jakarta-validated (mirror RegisterRequest.java).
- response/v1/RoleResponse, PermissionResponse — return id/name/description and (for role) nested permission names.

Repository additions:
- RoleRepository: existsByName, findAllWithPermissions() using @EntityGraph so list endpoints don't N+1.
- PermissionRepository: existsByName, findAllById(Collection) (already in JpaRepository).

Rules to enforce in services:
- Cannot delete ROLE_ADMIN or ROLE_USER (seeded names) — throw ResponseStatusException(CONFLICT).
- Cannot delete a role still assigned to any user.
- Cannot delete a permission still attached to a role.
- Cannot remove ROLE_ADMIN from the last admin — reuse UserRepository.countAdminUsers().
- Role/permission name is unique; surface as 409 via existing ExceptionHandlers.java.

 ---
2. LAZY + JOIN FETCH on the auth path

Currently User.roles and Role.permissions are FetchType.EAGER (User.java:54-59, Role.java:32-38). Every entity load drags the full graph; once we add list endpoints in §1 this becomes N+1.

Changes:
- User.java: @ManyToMany(fetch = LAZY) for roles.
- Role.java: @ManyToMany(fetch = LAZY) for permissions.
- UserRepository: add
  @Query("select distinct u from User u left join fetch u.roles r left join fetch r.permissions where u.email = :email")
  Optional<User> findByEmailWithAuthorities(String email);
- SecurityConfig.java:38-43 userDetailsService bean: call findByEmailWithAuthorities instead of findByEmail. This is the one hot path that needs the full graph; everything else can stay lazy inside a transaction.
- Verify JwtAuthenticationFilter.java still works (it calls UserDetailsService, so it inherits the fetch).
- UserResponse mapping (currently in user services) must run inside @Transactional(readOnly = true) or use the JOIN FETCH variant.

 ---
3. JWT secret + login lockout

Secret:
- Remove placeholder in src/main/resources/application.properties:30. Replace with spring.jwt.secret=${JWT_SECRET:} and fail fast at startup: in JwtServiceImpl constructor, throw IllegalStateException if blank. Document in
  README that JWT_SECRET must be set (≥ 32 bytes for HS256).
- Provide a dev value in application-dev.properties (new file, gitignored or committed with clear "dev only" marker).

Login lockout (Redis-backed, reuses existing RedisConfig.java):
- New service/security/LoginAttemptService(+Impl) with:
    - recordFailure(email) — INCR loginAttempts:{email}, set TTL 15 min on first increment.
    - isBlocked(email) — true when counter ≥ 5.
    - reset(email) — DEL on success.
- Hook into LoginServiceImpl: check isBlocked before authenticationManager.authenticate; catch BadCredentialsException → recordFailure → rethrow; on success → reset.
- Surface 429 TOO_MANY_REQUESTS via ExceptionHandlers.

 ---
4. Audit logging

Add an audit logger (separate SLF4J logger name AUDIT) and log structured events using MDC:
- auth.login.success (email, ip)
- auth.login.failure (email, reason, attemptCount)
- auth.login.blocked (email)
- rbac.role.created|updated|deleted (actor, target)
- rbac.permission.* same
- rbac.user.roles.changed (actor, userId, before, after)
- auth.access.denied (email, path, method) — via a custom AccessDeniedHandler registered in SecurityConfig.

Implement as a thin AuditLogger component injected into LoginServiceImpl, the new role/permission services, and the new AccessDeniedHandler. Existing logging conventions: project does not yet use MDC, so add request-id
propagation in JwtAuthenticationFilter (MDC.put("requestId", UUID.randomUUID().toString()) in a try/finally) — this is the minimal touch needed; structured JSON output can be added later via logback config.

 ---
5. Tests

Project has no security tests today (src/test/java/ has only a context-load + Testcontainers config). Add:

- AuthFlowIntegrationTest (@SpringBootTest + Testcontainers, reusing TestcontainersConfiguration): register → login → call /users/current with token → 200; bad token → 401; missing token → 401.
- AdminAuthorizationTest (@SpringBootTest): non-admin user gets 403 on /admin/**; admin gets 200.
- RoleControllerTest (@WebMvcTest(RoleController.class) + @WithMockUser(authorities = "admin:write")): CRUD happy path + delete-seeded-role-rejected + duplicate-name-rejected.
- PermissionControllerTest — analogous.
- LoginAttemptServiceTest (@DataRedisTest or unit with mocked RedisTemplate): 5 failures → blocked; success resets; TTL expires.
- UserAuthorityFetchTest: assert findByEmailWithAuthorities returns roles and permissions and does not trigger lazy proxies (use Hibernate Statistics or unproxy).

 ---
Critical files (modify)

- src/main/java/com/housi/backend/entity/User.java (line 54-59) — LAZY
- src/main/java/com/housi/backend/entity/Role.java (line 32-38) — LAZY
- src/main/java/com/housi/backend/repository/UserRepository.java — new JOIN FETCH query
- src/main/java/com/housi/backend/config/SecurityConfig.java (line 38-43) — use new query; register AccessDeniedHandler
- src/main/java/com/housi/backend/service/auth/LoginServiceImpl.java — lockout hook + audit
- src/main/java/com/housi/backend/service/security/JwtServiceImpl.java — fail-fast on empty secret
- src/main/resources/application.properties (line 30-31) — env var binding
- src/main/java/com/housi/backend/config/JwtAuthenticationFilter.java — MDC requestId
- src/main/java/com/housi/backend/exception/ExceptionHandlers.java — 409 & 429 mappings

Critical files (create)

- controller/v1/admin/{RoleController,PermissionController,UserRoleController}.java
- service/admin/role/{RoleService,RoleServiceImpl}.java
- service/admin/permission/{PermissionService,PermissionServiceImpl}.java
- service/security/{LoginAttemptService,LoginAttemptServiceImpl}.java
- service/audit/AuditLogger.java
- security/RestAccessDeniedHandler.java
- request/v1/admin/{RoleRequest,PermissionRequest,AssignRolesRequest,AssignPermissionsRequest}.java
- response/v1/{RoleResponse,PermissionResponse}.java
- 6 test classes listed in §5

 ---
Verification

1. ./gradlew test — all new tests green; existing context-load test still passes.
2. Boot with unset JWT_SECRET → app fails to start with clear error.
3. Boot with JWT_SECRET set + Postgres/Redis via compose.yaml/Testcontainers.
4. Manual Swagger flow:
- Register user → becomes admin (first user) → login → token.
- POST /admin/roles create ROLE_MANAGER with permissions user:read,user:write.
- PUT /admin/users/{id}/roles assign ROLE_MANAGER to a second user.
- That user can call /users/current but not /admin/**.
- DELETE /admin/roles/{ROLE_ADMIN id} → 409.
- 5 failed logins for same email → 429 for 15 min.
5. Tail logs and confirm AUDIT entries for each event above.
6. Enable Hibernate SQL logging and confirm a single SELECT (with joins) on login, no lazy-load exceptions on /users/current.