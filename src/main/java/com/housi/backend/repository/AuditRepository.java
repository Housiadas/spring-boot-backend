package com.housi.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.Audit;

@Repository
public interface AuditRepository extends JpaRepository<Audit, UUID> {}
