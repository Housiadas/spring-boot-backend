package com.housi.backend.listener;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.housi.backend.entity.Audit;
import com.housi.backend.entity.User;
import com.housi.backend.event.EntityAuditEvent;
import com.housi.backend.repository.AuditRepository;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Component
public class AuditEventListener {

    private final AuditRepository auditRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    public AuditEventListener(
            AuditRepository auditRepository, FindAuthenticatedUser findAuthenticatedUser) {
        this.auditRepository = auditRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onEntityAudit(EntityAuditEvent event) {
        User actor = findAuthenticatedUser.getAuthenticatedUser();

        Audit audit = new Audit();
        audit.setObjId(event.entityId());
        audit.setObjEntity(event.entityType());
        audit.setObjName(event.entityName());
        audit.setActorId(actor.getId());
        audit.setAction(event.action().getName());
        audit.setMessage(event.message());
        audit.setCreatedAt(LocalDateTime.now());

        auditRepository.save(audit);
    }
}
