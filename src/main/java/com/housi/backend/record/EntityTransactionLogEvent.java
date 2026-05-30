package com.housi.backend.record;

import com.housi.backend.enums.EntityTransactionLogEnum;

public record EntityTransactionLogEvent(
        EntityTransactionLogEnum operation, String entityName, String entitiesToLog) {
}
