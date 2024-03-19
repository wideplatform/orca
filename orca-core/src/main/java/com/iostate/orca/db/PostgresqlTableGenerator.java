package com.iostate.orca.db;

public class PostgresqlTableGenerator extends AbstractTableGenerator {

    @Override
    protected DbType dbType() {
        return DbType.POSTGRESQL;
    }

    @Override
    protected String autoIncrement() {
        return "GENERATED BY DEFAULT AS IDENTITY";
    }
}
