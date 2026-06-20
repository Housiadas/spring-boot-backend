package com.housi.backend.infrastructure.persistence.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.housi.backend.domain.model.Company;
import com.housi.backend.infrastructure.persistence.entity.CompanyEntity;

@Mapper(componentModel = "spring")
public interface CompanyPersistenceMapper {
    Company toDomain(CompanyEntity entity);

    CompanyEntity toEntity(Company domain);

    List<Company> toDomainList(List<CompanyEntity> entities);
}
