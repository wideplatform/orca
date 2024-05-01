package com.iostate.orca.metadata;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public enum SimpleDataType implements DataType {
    BOOLEAN(Boolean.class, boolean.class),
    LONG(Long.class, long.class),
    INT(Integer.class, int.class),

    DECIMAL(BigDecimal.class),

    STRING(String.class),

    DATE(LocalDate.class),
    TIME(LocalTime.class),
    DATETIME(Instant.class);

    private final Class<?>[] javaTypes;

    SimpleDataType(Class<?>... javaTypes) {
        this.javaTypes = javaTypes;
    }

    public Class<?> javaType() {
        return javaTypes[0];
    }

    @Override
    public String javaTypeName() {
        return javaType().getName();
    }

    public static SimpleDataType valueOf(Class<?> javaType) {
        for (SimpleDataType dataType : values()) {
            for (Class<?> cls : dataType.javaTypes) {
                if (cls == javaType || cls.isAssignableFrom(javaType)) {
                    return dataType;
                }
            }
        }

        return null;
    }
}
