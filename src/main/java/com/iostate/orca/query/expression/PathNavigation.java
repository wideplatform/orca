package com.iostate.orca.query.expression;

import com.iostate.orca.query.SqlBuilder;

public class PathNavigation implements Expression {

    private final String path;

    public PathNavigation(String path) {
        this.path = path;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        sqlBuilder.addObjectPath(path);
    }

    @Override
    public String toString() {
        return path;
    }
}
