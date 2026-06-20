package com.housi.backend.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.housi.backend.domain.model.Company;

public interface CompanyPort {
    Optional<Company> findById(UUID id);

    List<Company> findAll();

    List<Company> findAllByCreatedBy(String createdBy);

    boolean existsBySlug(String slug);

    boolean existsByFederalTaxId(String federalTaxId);

    Company save(Company company);

    void delete(Company company);
}
