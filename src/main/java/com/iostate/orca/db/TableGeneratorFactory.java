package com.iostate.orca.db;

public class TableGeneratorFactory {

    public static TableGenerator make(DbType dbType) {
        switch (dbType) {
            case ANSI:
            case H2:
                return new TableGeneratorImpl();
            default:
                throw new IllegalArgumentException(String.valueOf(dbType));
        }
    }
}
