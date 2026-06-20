package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company implements Serializable {

    @Serial private static final long serialVersionUID = 2137607105409362080L;

    private UUID id;
    private String slug;
    private String name;
    private String officialName;
    private String federalTaxId;
    private String stateTaxId;
    private String phone;
    private String email;
    private String addressStreet;
    private String addressStreetNumber;
    private String addressComplement;
    private String addressCityDistrict;
    private String addressPostCode;
    private String addressCity;
    private String addressStateCode;
    private String addressCountry;
    private BigDecimal addressLatitude;
    private BigDecimal addressLongitude;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Company(final UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Company{"
                + "id="
                + this.id
                + ", slug='"
                + this.slug
                + "', name='"
                + this.name
                + "', officialName='"
                + this.officialName
                + "', federalTaxId='"
                + this.federalTaxId
                + "', stateTaxId='"
                + this.stateTaxId
                + "', phone='"
                + this.phone
                + "', email='"
                + this.email
                + "', addressStreet='"
                + this.addressStreet
                + "', addressStreetNumber='"
                + this.addressStreetNumber
                + "', addressComplement='"
                + this.addressComplement
                + "', addressCityDistrict='"
                + this.addressCityDistrict
                + "', addressPostCode='"
                + this.addressPostCode
                + "', addressCity='"
                + this.addressCity
                + "', addressStateCode='"
                + this.addressStateCode
                + "', addressCountry='"
                + this.addressCountry
                + "', addressLatitude="
                + this.addressLatitude
                + ", addressLongitude="
                + this.addressLongitude
                + "', createdBy="
                + this.getCreatedBy()
                + ", updatedBy="
                + this.getUpdatedBy()
                + "', createdAt="
                + this.getCreatedAt()
                + ", updatedAt="
                + this.getUpdatedAt()
                + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Company other)) {
            return false;
        }
        return this.getId() != null && this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    public boolean is(final String slug) {
        return StringUtils.isNotBlank(this.slug) && this.slug.equals(slug);
    }
}
