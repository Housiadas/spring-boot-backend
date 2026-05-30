package com.housi.backend.mappers;


import com.housi.backend.entity.ApiKey;
import com.housi.backend.mappers.base.ManagementBaseMapper;
import com.housi.backend.request.management.CreateApiKeyManagementRequest;
import com.housi.backend.request.management.UpdateApiKeyManagementRequest;
import com.housi.backend.response.management.ApikeyManagementResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApiKeyMapper
    extends ManagementBaseMapper<
            ApiKey,
            CreateApiKeyManagementRequest,
            UpdateApiKeyManagementRequest,
            ApikeyManagementResponse> {

  @Override
  ApiKey toEntity(CreateApiKeyManagementRequest request);

  @Override
  ApikeyManagementResponse toManagementResponse(ApiKey entity);
}
