package com.iostate.orca.db;

public class SchemaBuilderFactory {
    public static SchemaBuilder make(DbType dbType) {
        switch (dbType) {
            case H2, MYSQL -> {
                return new MysqlSchemaBuilder();
            }
            case POSTGRESQL -> {
                return new PostgresqlSchemaBuilder();
            }
            default -> throw new IllegalArgumentException(String.valueOf(dbType));
        }
    }
}
