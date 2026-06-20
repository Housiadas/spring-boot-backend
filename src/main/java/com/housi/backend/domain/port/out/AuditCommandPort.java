package com.housi.backend.domain.port.out;

import com.housi.backend.domain.model.Audit;

public interface AuditCommandPort {
    Audit save(Audit audit);
}
