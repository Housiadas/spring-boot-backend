package com.housi.backend.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Audit implements Serializable {

    @Serial private static final long serialVersionUID = 7213607105408362081L;

    private UUID id;
    private UUID objId;
    private String objEntity;
    private String objName;
    private UUID actorId;
    private String action;
    private String data;
    private String message;
    private LocalDateTime createdAt;
}
