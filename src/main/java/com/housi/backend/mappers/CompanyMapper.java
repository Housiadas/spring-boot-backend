package com.housi.backend.mappers;

import com.housi.backend.entity.Company;
import com.housi.backend.mappers.base.ManagementBaseMapper;
import com.housi.backend.request.management.CreateCompanyManagementRequest;
import com.housi.backend.request.management.UpdateCompanyManagementRequest;
import com.housi.backend.response.management.CompanyManagementResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper
    extends ManagementBaseMapper<
            Company,
            CreateCompanyManagementRequest,
            UpdateCompanyManagementRequest,
            CompanyManagementResponse> {}
