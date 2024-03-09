package com.iostate.orca.db;

import java.util.Objects;

public enum DbType {
    ANSI, H2, MYSQL;

    public static DbType of(String name) {
        Objects.requireNonNull(name, "name");
        return valueOf(name.toUpperCase());
    }
}
