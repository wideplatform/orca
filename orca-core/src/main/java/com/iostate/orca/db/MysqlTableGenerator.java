package com.iostate.orca.db;

public class MysqlTableGenerator extends AbstractTableGenerator {

    @Override
    protected DbType dbType() {
        return DbType.MYSQL;
    }

    @Override
    protected String autoIncrement() {
        return "AUTO_INCREMENT";
    }
}
