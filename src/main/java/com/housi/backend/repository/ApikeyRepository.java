package com.housi.backend.repository;

import com.housi.backend.entity.ApiKey;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApikeyRepository extends JpaRepository<ApiKey, UUID> {

  String CACHE_NAME = "apiKey";

  ApiKey findFirstByCompanyIdAndIsActive(Long companyId, boolean isActive);

  @Cacheable(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #key}")
  Optional<ApiKey> findByKeyAndIsActive(String key, boolean isActive);

  @Caching(evict = {@CacheEvict(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #entity.key}")})
  @Override
  <S extends ApiKey> @NonNull S save(@NonNull S entity);

  @Caching(evict = {@CacheEvict(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #entity.key}")})
  @Override
  void delete(@NonNull ApiKey entity);
}
