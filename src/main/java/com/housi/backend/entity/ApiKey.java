package com.housi.backend.entity;

import java.io.Serial;
import java.util.UUID;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.housi.backend.entity.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = ApiKey.TABLE_NAME)
public class ApiKey extends BaseEntity {
    public static final String TABLE_NAME = "api_key";

    @Serial private static final long serialVersionUID = -3552577854495026179L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @JoinColumn(nullable = false)
    private Long companyId;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String key;

    @Column(nullable = false)
    private Boolean isActive;

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Apikey{"
                + "id="
                + this.id
                + ", companyId="
                + this.companyId
                + ", name='"
                + this.name
                + "', isActive="
                + this.isActive
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
        if (!(o instanceof final ApiKey other)) {
            return false;
        }
        return this.getId() != null && this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
