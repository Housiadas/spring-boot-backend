# Spring Boot Backend

## Development
We are using testcontainers, they will be booted when we start the app

## Test

```
make test
```

## Hexagonal Architecture
```
 com.housi.backend
  │
  ├── domain/                          ← pure Java, zero framework dependencies
  │   ├── model/                       ← domain objects (POJOs, no @Entity)
  │   │   ├── User.java
  │   │   ├── Company.java
  │   │   ├── Role.java                ← implements GrantedAuthority (security contract)
  │   │   ├── Permission.java          ← implements GrantedAuthority (security contract)
  │   │   └── Audit.java
  │   ├── port/
  │   │   └── out/                     ← interfaces the domain defines, infra implements
  │   │       ├── UserPort.java
  │   │       ├── CompanyPort.java
  │   │       ├── RolePort.java
  │   │       ├── PermissionPort.java
  │   │       └── AuditPort.java
  │   ├── event/                       ← domain events
  │   │   └── EntityAuditEvent.java
  │   ├── exception/                   ← domain-level exceptions (no HTTP semantics here)
  │   │   ├── ResourceNotFoundException.java
  │   │   ├── ConflictException.java
  │   │   └── ...
  │   └── enums/
  │       ├── RoleEnum.java
  │       └── EntityTransactionAuditEnum.java
  │
  ├── usecase/                         ← orchestrates domain + ports, no framework coupling
  │   ├── auth/
  │   │   ├── LoginUseCase.java
  │   │   └── RegisterUseCase.java
  │   ├── user/
  │   │   ├── GetCurrentUserUseCase.java
  │   │   ├── ChangePasswordUseCase.java
  │   │   ├── DeleteUserUseCase.java
  │   │   ├── UserAdminUseCase.java
  │   │   └── LastAdminGuard.java
  │   ├── company/
  │   │   ├── UserCompanyUseCase.java
  │   │   ├── CompanyAdminUseCase.java
  │   │   ├── CompanyConflictGuard.java
  │   │   └── command/
  │   │       ├── CreateCompanyCommand.java
  │   │       └── UpdateCompanyCommand.java
  │   ├── role/
  │   │   └── RoleAdminUseCase.java
  │   ├── permission/
  │   │   └── PermissionAdminUseCase.java
  │   ├── userrole/
  │   │   └── UserRoleAdminUseCase.java
  │   ├── audit/
  │   │   └── AuditAdminUseCase.java
  │   └── webhook/
  │       └── WebhookSiteUseCase.java
  │
  └── infrastructure/                  ← all framework, IO, and delivery concerns
      ├── config/                      ← Spring @Configuration classes
      │   ├── SecurityConfiguration.java
      │   ├── RedisConfiguration.java
      │   ├── SwaggerConfiguration.java
      │   └── InterceptorConfiguration.java
      │
      ├── persistence/                 ← JPA adapter; implements domain ports
      │   ├── entity/                  ← @Entity classes (JPA-specific, framework coupling lives here)
      │   │   ├── UserEntity.java      ← implements UserDetails (Spring Security)
      │   │   ├── CompanyEntity.java
      │   │   ├── RoleEntity.java      ← implements GrantedAuthority
      │   │   ├── PermissionEntity.java← implements GrantedAuthority
      │   │   └── AuditEntity.java
      │   ├── repository/              ← Spring Data JpaRepository interfaces (entity-typed)
      │   │   ├── UserJpaRepository.java
      │   │   ├── CompanyJpaRepository.java
      │   │   ├── RoleJpaRepository.java
      │   │   ├── PermissionJpaRepository.java
      │   │   └── AuditJpaRepository.java
      │   ├── adapter/                 ← implements domain out-ports; bridges repo + mapper
      │   │   ├── UserAdapter.java     ← implements UserPort
      │   │   ├── CompanyAdapter.java  ← implements CompanyPort (carries @Cacheable)
      │   │   ├── RoleAdapter.java     ← implements RolePort
      │   │   ├── PermissionAdapter.java← implements PermissionPort
      │   │   └── AuditAdapter.java   ← implements AuditPort
      │   └── mapper/                  ← Entity ↔ domain model (MapStruct)
      │       ├── UserPersistenceMapper.java
      │       ├── CompanyPersistenceMapper.java
      │       ├── RolePersistenceMapper.java
      │       ├── PermissionPersistenceMapper.java
      │       └── AuditPersistenceMapper.java
      │
      ├── web/                         ← HTTP delivery adapter
      │   ├── controller/v1/
      │   │   ├── admin/
      │   │   │   ├── UserAdminController.java
      │   │   │   ├── CompanyAdminController.java
      │   │   │   ├── RoleController.java
      │   │   │   ├── PermissionController.java
      │   │   │   └── AuditAdminController.java
      │   │   ├── AuthController.java
      │   │   ├── UserController.java
      │   │   ├── UserCompanyController.java
      │   │   └── PublicController.java
      │   ├── request/v1/              ← HTTP request DTOs
      │   ├── response/v1/             ← HTTP response DTOs
      │   ├── mapper/                  ← domain model → Response DTO (MapStruct)
      │   │   ├── UserMapper.java
      │   │   ├── CompanyMapper.java
      │   │   ├── RoleMapper.java
      │   │   ├── PermissionMapper.java
      │   │   └── AuditMapper.java
      │   ├── filter/
      │   │   ├── JwtAuthenticationFilter.java
      │   │   └── HttpRequestLoggingFilter.java
      │   ├── interceptor/
      │   │   ├── TimeExecutionInterceptor.java
      │   │   └── LogSlowResponseTimeInterceptor.java
      │   ├── advice/
      │   │   ├── ResponseHeaderAdvice.java
      │   │   └── GlobalExceptionHandler.java  ← maps domain exceptions → HTTP
      │   └── actuator/
      │       └── WebMvcPreStopHookEndpoint.java
      │
      ├── security/                    ← security infrastructure (JWT, rate-limit, auth)
      │   ├── UserDetailsServiceAdapter.java   ← loads UserEntity for Spring Security
      │   ├── FindAuthenticatedUser.java        ← maps UserEntity → domain User from context
      │   ├── JwtService.java
      │   ├── JwtAuthenticationEntryPoint.java
      │   └── LoginAttemptService.java
      │
      ├── messaging/                   ← event listeners and publishers
      │   └── AuditEventListener.java
      │
      ├── audit/                       ← structured audit logging helpers
      │   └── AuditLogger.java
      │
      └── client/                      ← outbound HTTP clients
          ├── http/WebhookSiteHttpClient.java
          └── slack/SlackAlertClient.java
```

## Dependency flow

```
Controller → UseCase → Port (domain interface)
                            ↑
                       Adapter (infra, implements port)
                            ↓
                       JpaRepository → Entity → Database
```

The domain (`model/`, `port/`, `usecase/`) has zero dependency on JPA, Hibernate, or Spring Data.
All framework coupling is contained inside `infrastructure/`.
