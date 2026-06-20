package com.housi.backend.usecase.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.model.Audit;
import com.housi.backend.domain.port.out.AuditPort;

@Service
@Transactional(readOnly = true)
public class AuditAdminUseCase {

    private final AuditPort auditPort;

    public AuditAdminUseCase(AuditPort auditPort) {
        this.auditPort = auditPort;
    }

    public Page<Audit> getAll(Pageable pageable) {
        return auditPort.findAll(pageable);
    }
}
