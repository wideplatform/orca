package com.iostate.orca.db;

public class TableGeneratorFactory {

    public static TableGenerator make(DbType dbType) {
        switch (dbType) {
            case H2, MYSQL -> {
                return new MysqlTableGenerator();
            }
            case POSTGRESQL -> {
                return new PostgresqlTableGenerator();
            }
            default -> throw new IllegalArgumentException(String.valueOf(dbType));
        }
    }
}
