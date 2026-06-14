package com.housi.backend.controller.response.v1;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String slug,
        String name,
        String officialName,
        String federalTaxId,
        String stateTaxId,
        String phone,
        String email,
        String addressStreet,
        String addressStreetNumber,
        String addressComplement,
        String addressCityDistrict,
        String addressPostCode,
        String addressCity,
        String addressStateCode,
        String addressCountry,
        BigDecimal addressLatitude,
        BigDecimal addressLongitude,
        String createdBy,
        String updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
