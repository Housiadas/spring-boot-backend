package com.housi.backend.infrastructure.web.controller.v1.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.infrastructure.web.mapper.AuditMapper;
import com.housi.backend.infrastructure.web.response.shared.ApiListPaginationSuccess;
import com.housi.backend.infrastructure.web.response.v1.AuditResponse;
import com.housi.backend.usecase.audit.AuditAdminUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Audits", description = "View audit logs")
@RestController
@RequestMapping(AuditAdminController.BASE_URL)
public class AuditAdminController {
    public static final String BASE_URL = AppUrls.V1_ADMIN + "/audits";

    private final AuditAdminUseCase auditAdminUseCase;
    private final AuditMapper auditMapper;

    public AuditAdminController(AuditAdminUseCase auditAdminUseCase, AuditMapper auditMapper) {
        this.auditAdminUseCase = auditAdminUseCase;
        this.auditMapper = auditMapper;
    }

    @Operation(summary = "List all audit entries")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public ApiListPaginationSuccess<AuditResponse> getAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiListPaginationSuccess.of(
                auditAdminUseCase.getAll(pageable).map(auditMapper::toResponse));
    }
}
