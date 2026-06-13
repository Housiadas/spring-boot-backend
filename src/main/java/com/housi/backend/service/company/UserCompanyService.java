package com.housi.backend.service.company;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.controller.request.v1.CreateCompanyRequest;
import com.housi.backend.controller.request.v1.UpdateCompanyRequest;
import com.housi.backend.entity.Company;
import com.housi.backend.entity.User;
import com.housi.backend.repository.CompanyRepository;
import com.housi.backend.service.audit.AuditLogger;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Service
public class UserCompanyService {

    private final CompanyRepository companyRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final AuditLogger auditLogger;

    public UserCompanyService(
            CompanyRepository companyRepository,
            FindAuthenticatedUser findAuthenticatedUser,
            AuditLogger auditLogger) {
        this.companyRepository = companyRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.auditLogger = auditLogger;
    }

    @Transactional(readOnly = true)
    public List<Company> getMyCompanies() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        return companyRepository.findAllByCreatedBy(user.getEmail());
    }

    @Transactional(readOnly = true)
    public Company getMyCompanyById(UUID id) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        return company;
    }

    @Transactional
    public Company register(CreateCompanyRequest request) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        if (companyRepository.existsBySlug(request.slug())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Company slug already exists: " + request.slug());
        }
        if (companyRepository.existsByFederalTaxId(request.federalTaxId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Company with this federal tax ID already exists: " + request.federalTaxId());
        }
        Company company = buildFromRequest(new Company(), request);
        Company saved = companyRepository.save(company);
        auditLogger.companyUserRegistered(saved.getSlug(), user.getEmail());
        return saved;
    }

    @Transactional
    public Company update(UUID id, UpdateCompanyRequest request) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        if (!request.slug().equals(company.getSlug())
                && companyRepository.existsBySlug(request.slug())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Company slug already exists: " + request.slug());
        }
        if (!request.federalTaxId().equals(company.getFederalTaxId())
                && companyRepository.existsByFederalTaxId(request.federalTaxId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Company with this federal tax ID already exists: " + request.federalTaxId());
        }
        applyUpdate(company, request);
        Company saved = companyRepository.save(company);
        auditLogger.companyUserUpdated(saved.getSlug(), user.getEmail());
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        Company company = require(id);
        verifyOwnership(company, user);
        auditLogger.companyUserDeleted(company.getSlug(), user.getEmail());
        companyRepository.delete(company);
    }

    private Company require(UUID id) {
        return companyRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Company not found"));
    }

    private void verifyOwnership(Company company, User user) {
        if (!user.getEmail().equals(company.getCreatedBy())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this company");
        }
    }

    private void applyUpdate(Company company, UpdateCompanyRequest request) {
        company.setSlug(request.slug());
        company.setName(request.name());
        company.setFederalTaxId(request.federalTaxId());
        if (request.officialName() != null) company.setOfficialName(request.officialName());
        if (request.stateTaxId() != null) company.setStateTaxId(request.stateTaxId());
        if (request.phone() != null) company.setPhone(request.phone());
        if (request.email() != null) company.setEmail(request.email());
        if (request.addressStreet() != null) company.setAddressStreet(request.addressStreet());
        if (request.addressStreetNumber() != null)
            company.setAddressStreetNumber(request.addressStreetNumber());
        if (request.addressComplement() != null)
            company.setAddressComplement(request.addressComplement());
        if (request.addressCityDistrict() != null)
            company.setAddressCityDistrict(request.addressCityDistrict());
        if (request.addressPostCode() != null)
            company.setAddressPostCode(request.addressPostCode());
        if (request.addressCity() != null) company.setAddressCity(request.addressCity());
        if (request.addressStateCode() != null)
            company.setAddressStateCode(request.addressStateCode());
        if (request.addressCountry() != null) company.setAddressCountry(request.addressCountry());
        if (request.addressLatitude() != null)
            company.setAddressLatitude(request.addressLatitude());
        if (request.addressLongitude() != null)
            company.setAddressLongitude(request.addressLongitude());
    }

    private Company buildFromRequest(Company company, CreateCompanyRequest request) {
        company.setSlug(request.slug());
        company.setName(request.name());
        company.setOfficialName(request.officialName());
        company.setFederalTaxId(request.federalTaxId());
        company.setStateTaxId(request.stateTaxId());
        company.setPhone(request.phone());
        company.setEmail(request.email());
        company.setAddressStreet(request.addressStreet());
        company.setAddressStreetNumber(request.addressStreetNumber());
        company.setAddressComplement(request.addressComplement());
        company.setAddressCityDistrict(request.addressCityDistrict());
        company.setAddressPostCode(request.addressPostCode());
        company.setAddressCity(request.addressCity());
        company.setAddressStateCode(request.addressStateCode());
        company.setAddressCountry(request.addressCountry());
        company.setAddressLatitude(request.addressLatitude());
        company.setAddressLongitude(request.addressLongitude());
        return company;
    }
}
