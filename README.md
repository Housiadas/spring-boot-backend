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
  │   ├── model/                       ← domain objects (POJOs, not @Entity)
  │   │   ├── User.java
  │   │   ├── Company.java
  │   │   ├── Role.java
  │   │   ├── Permission.java
  │   │   └── Audit.java
  │   ├── port/
  │   │   └── out/                     ← interfaces the domain defines, infra implements
  │   │       ├── UserPort.java
  │   │       ├── CompanyPort.java
  │   │       ├── RolePort.java
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
  ├── usecase/                         ← aggregation layer; orchestrates domain + ports
  │   ├── auth/
  │   │   ├── LoginUseCase.java        ← calls UserPort, JwtPort, LoginAttemptPort
  │   │   └── RegisterUseCase.java
  │   ├── user/
  │   │   ├── GetCurrentUserUseCase.java
  │   │   ├── ChangePasswordUseCase.java
  │   │   ├── DeleteUserUseCase.java
  │   │   └── GetUserAdminUseCase.java
  │   ├── company/
  │   │   ├── CreateCompanyUseCase.java
  │   │   ├── UpdateCompanyUseCase.java
  │   │   └── GetCompanyUseCase.java
  │   ├── role/
  │   │   └── RoleAdminUseCase.java
  │   ├── permission/
  │   │   └── PermissionAdminUseCase.java
  │   └── audit/
  │       └── AuditAdminUseCase.java
  │
  └── infrastructure/                  ← all framework, IO, and delivery concerns
      ├── config/                      ← Spring @Configuration classes
      │   ├── SecurityConfiguration.java
      │   ├── RedisConfiguration.java
      │   ├── SwaggerConfiguration.java
      │   └── InterceptorConfiguration.java
      │
      ├── persistence/                 ← JPA adapter; implements domain ports
      │   ├── entity/                  ← @Entity classes (JPA-specific)
      │   │   ├── UserEntity.java
      │   │   ├── CompanyEntity.java
      │   │   └── ...
      │   ├── repository/              ← Spring Data JpaRepository interfaces
      │   │   ├── UserJpaRepository.java
      │   │   ├── CompanyJpaRepository.java
      │   │   └── ...
      │   ├── adapter/                 ← implements domain out-ports
      │   │   ├── UserAdapter.java     ← implements UserPort
      │   │   ├── CompanyAdapter.java
      │   │   └── ...
      │   └── mapper/                  ← Entity ↔ domain model mapping
      │       ├── UserPersistenceMapper.java
      │       └── ...
      │
      ├── web/                         ← HTTP delivery adapter
      │   ├── controller/
      │   │   └── v1/
      │   │       ├── admin/
      │   │       │   ├── UserAdminController.java   ← calls usecase directly
      │   │       │   ├── CompanyAdminController.java
      │   │       │   └── ...
      │   │       ├── AuthController.java
      │   │       ├── UserController.java
      │   │       └── UserCompanyController.java
      │   ├── request/v1/              ← HTTP request DTOs
      │   ├── response/v1/             ← HTTP response DTOs
      │   ├── mapper/                  ← Request/Response ↔ domain model
      │   │   ├── UserWebMapper.java
      │   │   └── ...
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
      ├── security/                    ← security infrastructure (JWT, rate-limit)
      │   ├── JwtService.java
      │   ├── JwtAuthenticationEntryPoint.java
      │   └── LoginAttemptService.java
      │
      ├── messaging/                   ← event listeners and publishers
      │   └── AuditEventListener.java
      │
      └── client/                      ← outbound HTTP clients
          ├── webhook/WebhookSiteHttpClient.java
          └── slack/SlackAlertClient.java
```