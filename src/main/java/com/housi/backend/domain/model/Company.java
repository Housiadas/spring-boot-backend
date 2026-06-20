package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Company implements Serializable {

    @Serial private static final long serialVersionUID = 2137607105409362080L;

    @EqualsAndHashCode.Include
    UUID id;
    String slug;
    String name;
    String officialName;
    String federalTaxId;
    String stateTaxId;
    String phone;
    String email;
    String addressStreet;
    String addressStreetNumber;
    String addressComplement;
    String addressCityDistrict;
    String addressPostCode;
    String addressCity;
    String addressStateCode;
    String addressCountry;
    BigDecimal addressLatitude;
    BigDecimal addressLongitude;
    String createdBy;
    String updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
