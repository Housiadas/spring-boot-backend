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
        Company company = applyCreate(new Company(), cmd);
        Company saved = companyPort.save(company);
        auditLogger.companyUserRegistered(saved.getSlug(), user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(), "Company", saved.getSlug(), EntityTransactionAuditEnum.CREATE));
        return saved;
    }

    @Transactional
    public Company update(UUID id, UpdateCompanyCommand cmd) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        conflictGuard.assertSlugAvailable(cmd.slug(), company.getSlug());
        conflictGuard.assertFederalTaxIdAvailable(cmd.federalTaxId(), company.getFederalTaxId());
        applyUpdate(company, cmd);
        Company saved = companyPort.save(company);
        auditLogger.companyUserUpdated(saved.getSlug(), user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(), "Company", saved.getSlug(), EntityTransactionAuditEnum.UPDATE));
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        auditLogger.companyUserDeleted(company.getSlug(), user.getEmail());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        company.getId(), "Company", company.getSlug(), EntityTransactionAuditEnum.DELETE));
        companyPort.delete(company);
    }

    private Company require(UUID id) {
        return companyPort
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ProblemType.COMPANY_NOT_FOUND, "Company with id '" + id + "' not found."));
    }

    private void verifyOwnership(Company company, User user) {
        if (!user.getEmail().equals(company.getCreatedBy())) {
            throw new NotAllowedException(ProblemType.ACCESS_DENIED, "You do not own this company.");
        }
    }

    private Company applyCreate(Company company, CreateCompanyCommand cmd) {
        company.setSlug(cmd.slug());
        company.setName(cmd.name());
        company.setOfficialName(cmd.officialName());
        company.setFederalTaxId(cmd.federalTaxId());
        company.setStateTaxId(cmd.stateTaxId());
        company.setPhone(cmd.phone());
        company.setEmail(cmd.email());
        company.setAddressStreet(cmd.addressStreet());
        company.setAddressStreetNumber(cmd.addressStreetNumber());
        company.setAddressComplement(cmd.addressComplement());
        company.setAddressCityDistrict(cmd.addressCityDistrict());
        company.setAddressPostCode(cmd.addressPostCode());
        company.setAddressCity(cmd.addressCity());
        company.setAddressStateCode(cmd.addressStateCode());
        company.setAddressCountry(cmd.addressCountry());
        company.setAddressLatitude(cmd.addressLatitude());
        company.setAddressLongitude(cmd.addressLongitude());
        return company;
    }

    private void applyUpdate(Company company, UpdateCompanyCommand cmd) {
        company.setSlug(cmd.slug());
        company.setName(cmd.name());
        company.setFederalTaxId(cmd.federalTaxId());
        if (cmd.officialName() != null) company.setOfficialName(cmd.officialName());
        if (cmd.stateTaxId() != null) company.setStateTaxId(cmd.stateTaxId());
        if (cmd.phone() != null) company.setPhone(cmd.phone());
        if (cmd.email() != null) company.setEmail(cmd.email());
        if (cmd.addressStreet() != null) company.setAddressStreet(cmd.addressStreet());
        if (cmd.addressStreetNumber() != null) company.setAddressStreetNumber(cmd.addressStreetNumber());
        if (cmd.addressComplement() != null) company.setAddressComplement(cmd.addressComplement());
        if (cmd.addressCityDistrict() != null) company.setAddressCityDistrict(cmd.addressCityDistrict());
        if (cmd.addressPostCode() != null) company.setAddressPostCode(cmd.addressPostCode());
        if (cmd.addressCity() != null) company.setAddressCity(cmd.addressCity());
        if (cmd.addressStateCode() != null) company.setAddressStateCode(cmd.addressStateCode());
        if (cmd.addressCountry() != null) company.setAddressCountry(cmd.addressCountry());
        if (cmd.addressLatitude() != null) company.setAddressLatitude(cmd.addressLatitude());
        if (cmd.addressLongitude() != null) company.setAddressLongitude(cmd.addressLongitude());
    }
}
