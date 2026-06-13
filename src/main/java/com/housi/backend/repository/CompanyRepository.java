package com.housi.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    String CACHE_NAME = "company";

    @NonNull
    @Cacheable(value = CACHE_NAME, key = "{'byId', #id}")
    @Override
    Optional<Company> findById(@NonNull UUID id);

    @Cacheable(value = CACHE_NAME, key = "{'bySlug', #slug}")
    Optional<Company> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByFederalTaxId(String federalTaxId);

    boolean existsByStateTaxId(String stateTaxId);

    List<Company> findAllByCreatedBy(String createdBy);

    @Caching(
            evict = {
                @CacheEvict(value = CACHE_NAME, key = "{'byId', #entity.id}"),
                @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #entity.slug}"),
            })
    @NonNull
    @Override
    <S extends Company> S save(@NonNull S entity);

    @Caching(
            evict = {
                @CacheEvict(value = CACHE_NAME, key = "{'byId', #entity.id}"),
                @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #entity.slug}"),
            })
    @Override
    void delete(@NonNull Company entity);
}
