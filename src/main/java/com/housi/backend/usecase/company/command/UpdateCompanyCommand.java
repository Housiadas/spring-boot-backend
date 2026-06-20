package com.housi.backend.usecase.company.command;

import java.math.BigDecimal;

public record UpdateCompanyCommand(
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
        BigDecimal addressLongitude) {}
