package com.housi.backend.service.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Audit;
import com.housi.backend.repository.AuditRepository;

@Service
@Transactional(readOnly = true)
public class AuditAdminService {

    private final AuditRepository auditRepository;

    public AuditAdminService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public Page<Audit> getAll(Pageable pageable) {
        return auditRepository.findAll(pageable);
    }
}
