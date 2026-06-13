package com.housi.backend.controller.v1;

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
import com.housi.backend.service.company.UserCompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Companies", description = "Register and manage your companies")
@RestController
@RequestMapping(UserCompanyController.BASE_URL)
public class UserCompanyController {
    public static final String BASE_URL = AppUrls.V1_USERS + "/companies";

    private final UserCompanyService userCompanyService;
    private final CompanyMapper companyMapper;

    public UserCompanyController(
            UserCompanyService userCompanyService, CompanyMapper companyMapper) {
        this.userCompanyService = userCompanyService;
        this.companyMapper = companyMapper;
    }

    @Operation(summary = "List my companies")
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping
    public List<CompanyResponse> getMyCompanies() {
        return companyMapper.toResponseList(userCompanyService.getMyCompanies());
    }

    @Operation(summary = "Get my company by id")
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public CompanyResponse getMyCompanyById(@PathVariable UUID id) {
        return companyMapper.toResponse(userCompanyService.getMyCompanyById(id));
    }

    @Operation(summary = "Register a company")
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompanyResponse register(@Valid @RequestBody CreateCompanyRequest request) {
        return companyMapper.toResponse(userCompanyService.register(request));
    }

    @Operation(summary = "Update my company")
    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("/{id}")
    public CompanyResponse update(
            @PathVariable UUID id, @Valid @RequestBody UpdateCompanyRequest request) {
        return companyMapper.toResponse(userCompanyService.update(id, request));
    }

    @Operation(summary = "Delete my company")
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userCompanyService.delete(id);
    }
}
