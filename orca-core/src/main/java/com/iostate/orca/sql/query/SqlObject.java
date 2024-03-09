package com.iostate.orca.sql.query;

public class SqlObject {

    private final String sql;
    private final Object[] arguments;

    public SqlObject(String sql, Object[] arguments) {
        this.sql = sql;
        this.arguments = arguments;
    }

    public String getSql() {
        return sql;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
