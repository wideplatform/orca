package com.iostate.orca.sql.query;

public class SqlCondition {

    private final SqlExpression left;
    private final String operator;
    private final SqlExpression right;

    public SqlCondition(SqlExpression left, String operator, SqlExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return left + " " + operator + " " + right;
    }
}
