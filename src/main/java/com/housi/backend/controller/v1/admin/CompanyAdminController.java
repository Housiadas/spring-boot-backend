package com.housi.backend.controller.v1.admin;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.controller.mappers.CompanyMapper;
import com.housi.backend.controller.request.v1.CreateCompanyRequest;
import com.housi.backend.controller.request.v1.UpdateCompanyRequest;
import com.housi.backend.controller.response.v1.CompanyResponse;
import com.housi.backend.service.company.CompanyAdminService;

import com.housi.backend.controller.response.shared.ApiProblemDetail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Companies", description = "Manage companies")
@RestController
@RequestMapping(CompanyAdminController.BASE_URL)
public class CompanyAdminController {
    public static final String BASE_URL = AppUrls.V1_ADMIN + "/companies";

    private final CompanyAdminService companyAdminService;
    private final CompanyMapper companyMapper;

    public CompanyAdminController(
            CompanyAdminService companyAdminService, CompanyMapper companyMapper) {
        this.companyAdminService = companyAdminService;
        this.companyMapper = companyMapper;
    }

    @Operation(summary = "List all companies")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public List<CompanyResponse> getAll() {
        return companyMapper.toResponseList(companyAdminService.getAll());
    }

    @Operation(summary = "Get company by id")
    @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{id}")
    public CompanyResponse getById(@PathVariable UUID id) {
        return companyMapper.toResponse(companyAdminService.getById(id));
    }

    @Operation(summary = "Create a company")
    @ApiResponse(responseCode = "201", description = "Company created successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompanyResponse create(@Valid @RequestBody CreateCompanyRequest request) {
        return companyMapper.toResponse(companyAdminService.create(request));
    }

    @Operation(summary = "Update a company")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}")
    public CompanyResponse update(
            @PathVariable UUID id, @Valid @RequestBody UpdateCompanyRequest request) {
        return companyMapper.toResponse(companyAdminService.update(id, request));
    }

    @Operation(summary = "Delete a company")
    @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        companyAdminService.delete(id);
    }
}
