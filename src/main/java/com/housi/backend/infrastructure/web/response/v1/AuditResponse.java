package com.housi.backend.infrastructure.web.response.v1;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditResponse(
        UUID id,
        UUID objId,
        String objEntity,
        String objName,
        UUID actorId,
        String action,
        String data,
        String message,
        LocalDateTime createdAt) {}
