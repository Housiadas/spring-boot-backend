package com.housi.backend.mappers;

import com.housi.backend.entity.Company;
import com.housi.backend.mappers.base.ManagementBaseMapper;
import com.housi.backend.request.api.v1.CreateCompanyManagementRequest;
import com.housi.backend.request.api.v1.UpdateCompanyManagementRequest;
import com.housi.backend.response.api.v1.CompanyManagementResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper
    extends ManagementBaseMapper<
            Company,
            CreateCompanyManagementRequest,
            UpdateCompanyManagementRequest,
            CompanyManagementResponse> {}
