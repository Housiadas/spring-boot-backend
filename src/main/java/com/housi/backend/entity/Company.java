package com.housi.backend.entity;

import static org.apache.commons.lang3.StringUtils.getDigits;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = Company.TABLE_NAME)
public class Company implements Serializable {
    public static final String TABLE_NAME = "companies";

    @Serial private static final long serialVersionUID = 2137607105409362080L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column private String name;

    @Column(unique = true)
    private String officialName;

    @Column(nullable = false, unique = true)
    private String federalTaxId;

    @Column(unique = true)
    private String stateTaxId;

    @Column private String phone;
    @Column private String email;

    @Column private String addressStreet;
    @Column private String addressStreetNumber;
    @Column private String addressComplement;
    @Column private String addressCityDistrict;

    @Column private String addressPostCode;
    @Column private String addressCity;
    @Column private String addressStateCode;
    @Column private String addressCountry;

    @Column private BigDecimal addressLatitude;
    @Column private BigDecimal addressLongitude;

    @CreatedBy @Column private String createdBy;
    @LastModifiedBy @Column private String updatedBy;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Company(final UUID id) {
        this.id = id;
    }

    @PrePersist
    @PreUpdate
    private void preSave() {
        this.phone = getDigits(this.phone);
        this.federalTaxId = getDigits(this.federalTaxId);
        this.stateTaxId = getDigits(this.stateTaxId);
        this.addressPostCode = getDigits(this.addressPostCode);
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
