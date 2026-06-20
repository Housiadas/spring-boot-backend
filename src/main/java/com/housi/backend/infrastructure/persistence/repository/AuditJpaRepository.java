package com.housi.backend.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.infrastructure.persistence.entity.AuditEntity;

@Repository
public interface AuditJpaRepository extends JpaRepository<AuditEntity, UUID> {}
