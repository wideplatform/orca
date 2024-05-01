package com.iostate.orca.db;

public class MysqlSchemaBuilder extends AbstractSchemaBuilder {

    @Override
    protected TableGenerator getTableGenerator() {
        return TableGeneratorFactory.make(DbType.MYSQL);
    }
}
