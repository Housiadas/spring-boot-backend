package com.housi.backend.domain.port.out;

import com.housi.backend.domain.model.Company;

public interface CompanyCommandPort {
    Company save(Company company);

    void delete(Company company);
}
