package com.housi.backend.infrastructure.persistence.adapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.housi.backend.domain.model.Audit;
import com.housi.backend.domain.port.out.AuditCommandPort;
import com.housi.backend.domain.port.out.AuditQueryPort;
import com.housi.backend.infrastructure.persistence.mapper.AuditPersistenceMapper;
import com.housi.backend.infrastructure.persistence.repository.AuditJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditAdapter implements AuditQueryPort, AuditCommandPort {

    private final AuditJpaRepository repository;
    private final AuditPersistenceMapper mapper;

    @Override
    public Page<Audit> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Audit save(Audit audit) {
        return mapper.toDomain(repository.save(mapper.toEntity(audit)));
    }
}
