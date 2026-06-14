package com.housi.backend.service.company;

import org.springframework.stereotype.Component;

import com.housi.backend.exception.ConflictException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.repository.CompanyRepository;

@Component
public class CompanyConflictGuard {

    private final CompanyRepository companyRepository;

    public CompanyConflictGuard(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void assertSlugAvailable(String slug) {
        if (companyRepository.existsBySlug(slug)) {
            throw new ConflictException(ProblemType.DUPLICATE_SLUG, "Company slug already exists: " + slug);
        }
    }

    public void assertSlugAvailable(String newSlug, String currentSlug) {
        if (!newSlug.equals(currentSlug)) assertSlugAvailable(newSlug);
    }

    public void assertFederalTaxIdAvailable(String federalTaxId) {
        if (companyRepository.existsByFederalTaxId(federalTaxId)) {
            throw new ConflictException(ProblemType.DUPLICATE_FEDERAL_TAX_ID,
                    "Company with this federal tax ID already exists: " + federalTaxId);
        }
    }

    public void assertFederalTaxIdAvailable(String newId, String currentId) {
        if (!newId.equals(currentId)) assertFederalTaxIdAvailable(newId);
    }
}
