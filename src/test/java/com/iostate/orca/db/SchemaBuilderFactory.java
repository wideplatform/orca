package com.iostate.orca.db;

public class SchemaBuilderFactory {
    public static SchemaBuilder make(DbType dbType) {
        switch (dbType) {
            case ANSI:
                return new AnsiSchemaBuilder();
            case H2:
                return new H2SchemaBuilder();
            default:
                throw new IllegalArgumentException(String.valueOf(dbType));
        }
    }
}
