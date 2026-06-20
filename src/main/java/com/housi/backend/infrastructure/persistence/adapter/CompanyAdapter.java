package com.housi.backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import com.housi.backend.domain.model.Company;
import com.housi.backend.domain.port.out.CompanyPort;
import com.housi.backend.infrastructure.persistence.entity.CompanyEntity;
import com.housi.backend.infrastructure.persistence.mapper.CompanyPersistenceMapper;
import com.housi.backend.infrastructure.persistence.repository.CompanyJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompanyAdapter implements CompanyPort {

    static final String CACHE_NAME = "company";

    private final CompanyJpaRepository repository;
    private final CompanyPersistenceMapper mapper;

    @Override
    @Cacheable(value = CACHE_NAME, key = "{'byId', #id}")
    public Optional<Company> findById(@NonNull UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Company> findAll() {
        return mapper.toDomainList(repository.findAll());
    }

    @Override
    public List<Company> findAllByCreatedBy(String createdBy) {
        return mapper.toDomainList(repository.findAllByCreatedBy(createdBy));
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public boolean existsByFederalTaxId(String federalTaxId) {
        return repository.existsByFederalTaxId(federalTaxId);
    }

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = CACHE_NAME, key = "{'byId', #company.id}"),
                @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #company.slug}"),
            })
    public Company save(Company company) {
        CompanyEntity entity = mapper.toEntity(company);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = CACHE_NAME, key = "{'byId', #company.id}"),
                @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #company.slug}"),
            })
    public void delete(Company company) {
        repository.delete(mapper.toEntity(company));
    }
}
