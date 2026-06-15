package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = Audit.TABLE_NAME)
public class Audit implements Serializable {
    public static final String TABLE_NAME = "audit";

    @Serial private static final long serialVersionUID = 7213607105408362081L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "obj_id", nullable = false)
    private UUID objId;

    @Column(name = "obj_entity", nullable = false)
    private String objEntity;

    @Column(name = "obj_name", nullable = false)
    private String objName;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "jsonb")
    private String data;

    @Column private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
