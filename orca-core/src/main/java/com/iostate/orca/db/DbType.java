package com.iostate.orca.db;

import java.util.Objects;

public enum DbType {
    H2, MYSQL, POSTGRESQL;

    public static DbType of(String name) {
        Objects.requireNonNull(name, "name");
        return valueOf(name.toUpperCase());
    }
}
