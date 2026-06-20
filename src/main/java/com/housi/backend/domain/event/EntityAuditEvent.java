package com.housi.backend.domain.event;

import java.util.UUID;

import com.housi.backend.domain.enums.EntityTransactionAuditEnum;

public record EntityAuditEvent(
        UUID entityId,
        String entityType,
        String entityName,
        EntityTransactionAuditEnum action,
        String message) {

    public EntityAuditEvent(
            UUID entityId,
            String entityType,
            String entityName,
            EntityTransactionAuditEnum action) {
        this(entityId, entityType, entityName, action, null);
    }
}
