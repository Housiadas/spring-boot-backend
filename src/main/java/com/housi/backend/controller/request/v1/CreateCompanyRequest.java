package com.housi.backend.controller.request.v1;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
        @NotBlank String slug,
        @NotBlank String name,
        String officialName,
        @NotBlank String federalTaxId,
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
        BigDecimal addressLongitude) {}
