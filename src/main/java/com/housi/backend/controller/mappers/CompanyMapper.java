package com.housi.backend.controller.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.housi.backend.controller.response.v1.CompanyResponse;
import com.housi.backend.entity.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    List<CompanyResponse> toResponseList(List<Company> companies);
}
