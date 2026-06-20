package com.housi.backend.usecase.company;

import org.springframework.stereotype.Component;

import com.housi.backend.domain.exception.ConflictException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.port.out.CompanyQueryPort;

@Component
public class CompanyConflictGuard {

    private final CompanyQueryPort companyQueryPort;

    public CompanyConflictGuard(CompanyQueryPort companyQueryPort) {
        this.companyQueryPort = companyQueryPort;
    }

    public void assertSlugAvailable(String slug) {
        if (companyQueryPort.existsBySlug(slug)) {
            throw new ConflictException(
                    ProblemType.DUPLICATE_SLUG, "Company slug already exists: " + slug);
        }
    }

    public void assertSlugAvailable(String newSlug, String currentSlug) {
        if (!newSlug.equals(currentSlug)) assertSlugAvailable(newSlug);
    }

    public void assertFederalTaxIdAvailable(String federalTaxId) {
        if (companyQueryPort.existsByFederalTaxId(federalTaxId)) {
            throw new ConflictException(
                    ProblemType.DUPLICATE_FEDERAL_TAX_ID,
                    "Company with this federal tax ID already exists: " + federalTaxId);
        }
    }

    public void assertFederalTaxIdAvailable(String newId, String currentId) {
        if (!newId.equals(currentId)) assertFederalTaxIdAvailable(newId);
    }
}
