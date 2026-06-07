package com.housi.backend.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import com.housi.backend.entity.BaseEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtils {

    public static final String NULL = "null";

    public static String logId(final BaseEntity entity) {
        return entity != null && entity.getId() != null
                ? entity.getId().toString()
                : StringUtils.EMPTY;
    }

    public static String logIds(@NonNull final List<BaseEntity> entities) {
        if (entities.isEmpty()) {
            return List.of().toString();
        }

        return entities.stream()
                .map(e -> e.getId() != null ? e.getId().toString() : NULL)
                .toList()
                .toString();
    }
}
