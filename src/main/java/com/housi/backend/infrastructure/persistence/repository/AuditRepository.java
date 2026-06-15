package com.housi.backend.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.domain.model.Audit;
import com.housi.backend.domain.port.out.AuditPort;

@Repository
public interface AuditRepository extends JpaRepository<Audit, UUID>, AuditPort {}
