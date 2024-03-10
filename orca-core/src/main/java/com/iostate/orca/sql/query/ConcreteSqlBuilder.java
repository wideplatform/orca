package com.iostate.orca.sql.query;

import com.iostate.orca.query.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConcreteSqlBuilder implements SqlBuilder {

    private int selectColumnCount;
    private final List<Object> segments = new ArrayList<>();
    private final List<Object> arguments = new ArrayList<>();
    private final QueryTree queryTree;

    public ConcreteSqlBuilder(QueryTree queryTree) {
        this.queryTree = queryTree;
    }

    @Override
    public void addSelectColumn(String qualifier, String column) {
        if (selectColumnCount++ > 0) {
            addString(",");
        }
        addString(qualifier);
        addString(".");
        addString(column);
    }

    @Override
    public void addTableClause(String tableClause) {
        addString(tableClause);
    }

    @Override
    public void addString(String string) {
        segments.add(string);
    }

    @Override
    public void addArgument(Object value) {
        segments.add("?");
        arguments.add(value);
    }

    @Override
    public void addObjectPath(String objectPath) {
        addString(queryTree.resolveObjectPath(objectPath));
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public String toSql() {
        StringBuilder sb = new StringBuilder();
        for (Object segment : segments) {
            sb.append(segment);
        }
        return sb.toString();
    }
}
