package com.iostate.orca.api.query.expression;

import com.iostate.orca.api.query.SqlBuilder;

public class SingleValueBinding implements Expression {

    private final Object value;

    public SingleValueBinding(Object value) {
        this.value = value;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        sqlBuilder.addArgument(value);
    }

    @Override
    public String toString() {
        if (value instanceof CharSequence) {
            return "'" + value + "'";
        }
        return String.valueOf(value);
    }
}
