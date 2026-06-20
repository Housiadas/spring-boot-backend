package com.housi.backend.usecase.company;

import org.springframework.stereotype.Component;

import com.housi.backend.domain.exception.ConflictException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.port.out.CompanyPort;

@Component
public class CompanyConflictGuard {

    private final CompanyPort companyPort;

    public CompanyConflictGuard(CompanyPort companyPort) {
        this.companyPort = companyPort;
    }

    public void assertSlugAvailable(String slug) {
        if (companyPort.existsBySlug(slug)) {
            throw new ConflictException(
                    ProblemType.DUPLICATE_SLUG, "Company slug already exists: " + slug);
        }
    }

    public void assertSlugAvailable(String newSlug, String currentSlug) {
        if (!newSlug.equals(currentSlug)) assertSlugAvailable(newSlug);
    }

    public void assertFederalTaxIdAvailable(String federalTaxId) {
        if (companyPort.existsByFederalTaxId(federalTaxId)) {
            throw new ConflictException(
                    ProblemType.DUPLICATE_FEDERAL_TAX_ID,
                    "Company with this federal tax ID already exists: " + federalTaxId);
        }
    }

    public void assertFederalTaxIdAvailable(String newId, String currentId) {
        if (!newId.equals(currentId)) assertFederalTaxIdAvailable(newId);
    }
}
