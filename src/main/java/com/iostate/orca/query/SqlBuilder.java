package com.iostate.orca.query;

public interface SqlBuilder {

    void addSelectColumn(String qualifier, String column);

    void addTableClause(String tableClause);

    void addString(String string);

    void addArgument(Object value);

    void addObjectPath(String objectPath);
}
