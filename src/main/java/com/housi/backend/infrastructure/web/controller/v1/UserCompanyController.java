package com.housi.backend.infrastructure.web.controller.v1;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.infrastructure.web.mapper.CompanyMapper;
import com.housi.backend.infrastructure.web.request.v1.CreateCompanyRequest;
import com.housi.backend.infrastructure.web.request.v1.UpdateCompanyRequest;
import com.housi.backend.infrastructure.web.response.shared.ApiProblemDetail;
import com.housi.backend.infrastructure.web.response.v1.CompanyResponse;
import com.housi.backend.usecase.company.UserCompanyUseCase;
import com.housi.backend.usecase.company.command.CreateCompanyCommand;
import com.housi.backend.usecase.company.command.UpdateCompanyCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Companies", description = "Register and manage your companies")
@RestController
@RequestMapping(UserCompanyController.BASE_URL)
public class UserCompanyController {
    public static final String BASE_URL = AppUrls.V1_USERS + "/companies";

    private final UserCompanyUseCase userCompanyUseCase;
    private final CompanyMapper companyMapper;

    public UserCompanyController(
            UserCompanyUseCase userCompanyUseCase, CompanyMapper companyMapper) {
        this.userCompanyUseCase = userCompanyUseCase;
        this.companyMapper = companyMapper;
    }

    @Operation(summary = "List my companies")
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping
    public List<CompanyResponse> getMyCompanies() {
        return companyMapper.toResponseList(userCompanyUseCase.getMyCompanies());
    }

    @Operation(summary = "Get my company by id")
    @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public CompanyResponse getMyCompanyById(@PathVariable UUID id) {
        return companyMapper.toResponse(userCompanyUseCase.getMyCompanyById(id));
    }

    @Operation(summary = "Register a company")
    @ApiResponse(responseCode = "201", description = "Company created successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompanyResponse register(@Valid @RequestBody CreateCompanyRequest request) {
        return companyMapper.toResponse(userCompanyUseCase.register(toCommand(request)));
    }

    @Operation(summary = "Update my company")
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
    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("/{id}")
    public CompanyResponse update(
            @PathVariable UUID id, @Valid @RequestBody UpdateCompanyRequest request) {
        return companyMapper.toResponse(userCompanyUseCase.update(id, toCommand(request)));
    }

    @Operation(summary = "Delete my company")
    @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userCompanyUseCase.delete(id);
    }

    private CreateCompanyCommand toCommand(CreateCompanyRequest r) {
        return new CreateCompanyCommand(
                r.slug(), r.name(), r.officialName(), r.federalTaxId(), r.stateTaxId(),
                r.phone(), r.email(), r.addressStreet(), r.addressStreetNumber(),
                r.addressComplement(), r.addressCityDistrict(), r.addressPostCode(),
                r.addressCity(), r.addressStateCode(), r.addressCountry(),
                r.addressLatitude(), r.addressLongitude());
    }

    private UpdateCompanyCommand toCommand(UpdateCompanyRequest r) {
        return new UpdateCompanyCommand(
                r.slug(), r.name(), r.officialName(), r.federalTaxId(), r.stateTaxId(),
                r.phone(), r.email(), r.addressStreet(), r.addressStreetNumber(),
                r.addressComplement(), r.addressCityDistrict(), r.addressPostCode(),
                r.addressCity(), r.addressStateCode(), r.addressCountry(),
                r.addressLatitude(), r.addressLongitude());
    }
}
