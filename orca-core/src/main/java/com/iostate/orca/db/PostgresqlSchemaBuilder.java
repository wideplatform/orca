package com.iostate.orca.db;

public class PostgresqlSchemaBuilder extends AbstractSchemaBuilder {

    @Override
    protected TableGenerator getTableGenerator() {
        return TableGeneratorFactory.make(DbType.POSTGRESQL);
    }
}
