package com.housi.backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.infrastructure.persistence.entity.CompanyEntity;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {

    @NonNull Optional<CompanyEntity> findById(@NonNull UUID id);

    Optional<CompanyEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByFederalTaxId(String federalTaxId);

    boolean existsByStateTaxId(String stateTaxId);

    List<CompanyEntity> findAllByCreatedBy(String createdBy);
}
