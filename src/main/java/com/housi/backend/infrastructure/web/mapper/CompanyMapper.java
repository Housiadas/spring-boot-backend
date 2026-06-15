package com.housi.backend.infrastructure.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.housi.backend.domain.model.Company;
import com.housi.backend.infrastructure.web.response.v1.CompanyResponse;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    List<CompanyResponse> toResponseList(List<Company> companies);
}
