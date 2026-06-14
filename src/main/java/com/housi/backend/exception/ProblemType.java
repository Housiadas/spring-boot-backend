package com.housi.backend.exception;

import java.net.URI;

public enum ProblemType {

    // 400
    VALIDATION_FAILED("validation-failed", "Validation Failed"),
    INVALID_PASSWORD("invalid-password", "Invalid Password"),
    PASSWORD_MISMATCH("password-mismatch", "Password Mismatch"),
    PASSWORD_SAME_AS_CURRENT("password-same-as-current", "Password Same as Current"),
    UNKNOWN_ROLE("unknown-role", "Unknown Role"),
    UNKNOWN_PERMISSION("unknown-permission", "Unknown Permission"),
    PASSWORD_REQUIRED("password-required", "Password Required"),

    // 401
    AUTHENTICATION_REQUIRED("authentication-required", "Authentication Required"),
    INVALID_CREDENTIALS("invalid-credentials", "Invalid Credentials"),

    // 403
    ACCESS_DENIED("access-denied", "Access Denied"),

    // 404
    USER_NOT_FOUND("user-not-found", "User Not Found"),
    COMPANY_NOT_FOUND("company-not-found", "Company Not Found"),
    ROLE_NOT_FOUND("role-not-found", "Role Not Found"),
    PERMISSION_NOT_FOUND("permission-not-found", "Permission Not Found"),
    RESOURCE_NOT_FOUND("resource-not-found", "Resource Not Found"),

    // 409
    DUPLICATE_EMAIL("duplicate-email", "Duplicate Email"),
    DUPLICATE_SLUG("duplicate-slug", "Duplicate Company Slug"),
    DUPLICATE_FEDERAL_TAX_ID("duplicate-federal-tax-id", "Duplicate Federal Tax ID"),
    DUPLICATE_ROLE("duplicate-role", "Duplicate Role"),
    DUPLICATE_PERMISSION("duplicate-permission", "Duplicate Permission"),
    OPERATION_NOT_ALLOWED("operation-not-allowed", "Operation Not Allowed"),

    // 429
    TOO_MANY_REQUESTS("too-many-requests", "Too Many Requests"),

    // 500
    INTERNAL_ERROR("internal-error", "Internal Server Error");

    private static final String BASE_URN = "urn:problem-type:";

    private final URI type;
    private final String title;

    ProblemType(String code, String title) {
        this.type = URI.create(BASE_URN + code);
        this.title = title;
    }

    public URI getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
