package com.iostate.orca.db;

public class TableGeneratorFactory {

    public static TableGenerator make(DbType dbType) {
        return new TableGeneratorImpl();
    }
}
