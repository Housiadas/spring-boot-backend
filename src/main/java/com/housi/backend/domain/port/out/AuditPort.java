package com.housi.backend.domain.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.housi.backend.domain.model.Audit;

public interface AuditPort {
    Page<Audit> findAll(Pageable pageable);

    Audit save(Audit audit);
}
