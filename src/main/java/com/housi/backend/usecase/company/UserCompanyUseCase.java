package com.housi.backend.usecase.company;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.enums.EntityTransactionAuditEnum;
import com.housi.backend.domain.event.EntityAuditEvent;
import com.housi.backend.domain.exception.NotAllowedException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.exception.ResourceNotFoundException;
import com.housi.backend.domain.model.Company;
import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.CompanyPort;
import com.housi.backend.infrastructure.audit.AuditLogger;
import com.housi.backend.infrastructure.security.FindAuthenticatedUser;
import com.housi.backend.usecase.company.command.CreateCompanyCommand;
import com.housi.backend.usecase.company.command.UpdateCompanyCommand;

@Service
public class UserCompanyUseCase {

    private final CompanyPort companyPort;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;
    private final CompanyConflictGuard conflictGuard;

    public UserCompanyUseCase(
            CompanyPort companyPort,
            FindAuthenticatedUser findAuthenticatedUser,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher,
            CompanyConflictGuard conflictGuard) {
        this.companyPort = companyPort;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
        this.conflictGuard = conflictGuard;
    }

    @Transactional(readOnly = true)
    public List<Company> getMyCompanies() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        return companyPort.findAllByCreatedBy(user.getEmail());
    }

    @Transactional(readOnly = true)
    public Company getMyCompanyById(UUID id) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        return company;
    }

    @Transactional
    public Company register(CreateCompanyCommand cmd) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        conflictGuard.assertSlugAvailable(cmd.slug());
        conflictGuard.assertFederalTaxIdAvailable(cmd.federalTaxId());
        Company saved = companyPort.save(buildFromCreate(cmd));
        auditLogger.companyUserRegistered(saved.getSlug(), user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(),
                        "Company",
                        saved.getSlug(),
                        EntityTransactionAuditEnum.CREATE));
        return saved;
    }

    @Transactional
    public Company update(UUID id, UpdateCompanyCommand cmd) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        conflictGuard.assertSlugAvailable(cmd.slug(), company.getSlug());
        conflictGuard.assertFederalTaxIdAvailable(cmd.federalTaxId(), company.getFederalTaxId());
        Company saved = companyPort.save(applyUpdate(company, cmd));
        auditLogger.companyUserUpdated(saved.getSlug(), user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(),
                        "Company",
                        saved.getSlug(),
                        EntityTransactionAuditEnum.UPDATE));
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        company.getId(),
                        "Company",
                        company.getSlug(),
                        EntityTransactionAuditEnum.DELETE));
        companyPort.delete(company);
    }

    private Company require(UUID id) {
        return companyPort
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        ProblemType.COMPANY_NOT_FOUND,
                                        "Company with id '" + id + "' not found."));
    }

    private void verifyOwnership(Company company, User user) {
        if (!user.getEmail().equals(company.getCreatedBy())) {
            throw new NotAllowedException(
                    ProblemType.ACCESS_DENIED, "You do not own this company.");
        }
    }

    private Company buildFromCreate(CreateCompanyCommand cmd) {
        return Company.builder()
                .slug(cmd.slug())
                .name(cmd.name())
                .officialName(cmd.officialName())
                .federalTaxId(cmd.federalTaxId())
                .stateTaxId(cmd.stateTaxId())
                .phone(cmd.phone())
                .email(cmd.email())
                .addressStreet(cmd.addressStreet())
                .addressStreetNumber(cmd.addressStreetNumber())
                .addressComplement(cmd.addressComplement())
                .addressCityDistrict(cmd.addressCityDistrict())
                .addressPostCode(cmd.addressPostCode())
                .addressCity(cmd.addressCity())
                .addressStateCode(cmd.addressStateCode())
                .addressCountry(cmd.addressCountry())
                .addressLatitude(cmd.addressLatitude())
                .addressLongitude(cmd.addressLongitude())
                .build();
    }

    private Company applyUpdate(Company company, UpdateCompanyCommand cmd) {
        Company.CompanyBuilder builder = company.toBuilder()
                .slug(cmd.slug())
                .name(cmd.name())
                .federalTaxId(cmd.federalTaxId());
        if (cmd.officialName() != null) builder.officialName(cmd.officialName());
        if (cmd.stateTaxId() != null) builder.stateTaxId(cmd.stateTaxId());
        if (cmd.phone() != null) builder.phone(cmd.phone());
        if (cmd.email() != null) builder.email(cmd.email());
        if (cmd.addressStreet() != null) builder.addressStreet(cmd.addressStreet());
        if (cmd.addressStreetNumber() != null) builder.addressStreetNumber(cmd.addressStreetNumber());
        if (cmd.addressComplement() != null) builder.addressComplement(cmd.addressComplement());
        if (cmd.addressCityDistrict() != null) builder.addressCityDistrict(cmd.addressCityDistrict());
        if (cmd.addressPostCode() != null) builder.addressPostCode(cmd.addressPostCode());
        if (cmd.addressCity() != null) builder.addressCity(cmd.addressCity());
        if (cmd.addressStateCode() != null) builder.addressStateCode(cmd.addressStateCode());
        if (cmd.addressCountry() != null) builder.addressCountry(cmd.addressCountry());
        if (cmd.addressLatitude() != null) builder.addressLatitude(cmd.addressLatitude());
        if (cmd.addressLongitude() != null) builder.addressLongitude(cmd.addressLongitude());
        return builder.build();
    }
}
