package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
public class Audit implements Serializable {

    @Serial private static final long serialVersionUID = 7213607105408362081L;

    @EqualsAndHashCode.Include
    UUID id;
    UUID objId;
    String objEntity;
    String objName;
    UUID actorId;
    String action;
    String data;
    String message;
    LocalDateTime createdAt;
}
