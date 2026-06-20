package com.housi.backend.infrastructure.messaging;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.housi.backend.domain.event.EntityAuditEvent;
import com.housi.backend.domain.model.Audit;
import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.AuditPort;
import com.housi.backend.infrastructure.security.FindAuthenticatedUser;

@Component
public class AuditEventListener {

    private final AuditPort auditPort;
    private final FindAuthenticatedUser findAuthenticatedUser;

    public AuditEventListener(AuditPort auditPort, FindAuthenticatedUser findAuthenticatedUser) {
        this.auditPort = auditPort;
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onEntityAudit(EntityAuditEvent event) {
        User actor = findAuthenticatedUser.getAuthenticatedUser();

        auditPort.save(
                Audit.builder()
                        .objId(event.entityId())
                        .objEntity(event.entityType())
                        .objName(event.entityName())
                        .actorId(actor.getId())
                        .action(event.action().getName())
                        .message(event.message())
                        .createdAt(LocalDateTime.now())
                        .build());
    }
}
