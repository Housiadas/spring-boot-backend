package com.housi.backend.controller.mappers;

import com.housi.backend.entity.Company;
import com.housi.backend.controller.mappers.base.ManagementBaseMapper;
import com.housi.backend.controller.request.v1.CreateCompanyManagementRequest;
import com.housi.backend.controller.request.v1.UpdateCompanyManagementRequest;
import com.housi.backend.controller.response.v1.CompanyManagementResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper
    extends ManagementBaseMapper<
            Company,
            CreateCompanyManagementRequest,
            UpdateCompanyManagementRequest,
            CompanyManagementResponse> {}
