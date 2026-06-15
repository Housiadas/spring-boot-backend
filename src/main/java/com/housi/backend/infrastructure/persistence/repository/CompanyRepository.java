package com.housi.backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.domain.model.Company;
import com.housi.backend.domain.port.out.CompanyPort;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID>, CompanyPort {

    String CACHE_NAME = "company";

    @NonNull
    @Override
    @Cacheable(value = CACHE_NAME, key = "{'byId', #id}")
    Optional<Company> findById(@NonNull UUID id);

    @Cacheable(value = CACHE_NAME, key = "{'bySlug', #slug}")
    Optional<Company> findBySlug(String slug);

    @Override
    boolean existsBySlug(String slug);

    @Override
    boolean existsByFederalTaxId(String federalTaxId);

    boolean existsByStateTaxId(String stateTaxId);

    @Override
    List<Company> findAllByCreatedBy(String createdBy);

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = CACHE_NAME, key = "{'byId', #entity.id}"),
                @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #entity.slug}"),
            })
    @NonNull
    <S extends Company> S save(@NonNull S entity);

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = CACHE_NAME, key = "{'byId', #entity.id}"),
                @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #entity.slug}"),
            })
    void delete(@NonNull Company entity);
}
