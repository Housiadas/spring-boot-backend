package com.housi.backend.infrastructure.persistence.entity;

import static org.apache.commons.lang3.StringUtils.getDigits;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

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
@Table(name = "companies")
public class CompanyEntity implements Serializable {

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

    @PrePersist
    @PreUpdate
    private void preSave() {
        this.phone = getDigits(this.phone);
        this.federalTaxId = getDigits(this.federalTaxId);
        this.stateTaxId = getDigits(this.stateTaxId);
        this.addressPostCode = getDigits(this.addressPostCode);
    }
}
