package com.iostate.orca.query.expression;

import com.iostate.orca.query.SqlBuilder;

import java.util.Collection;
import java.util.stream.Collectors;

public class MultiValueBinding implements Expression {

    private final Collection<Object> values;

    public MultiValueBinding(Collection<Object> values) {
        this.values = values;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        sqlBuilder.addString("(");
        int count = 0;
        for (Object value : values) {
            if (count > 0) {
                sqlBuilder.addString(",");
            }
            sqlBuilder.addArgument(value);
            count++;
        }
        sqlBuilder.addString(")");
    }

    @Override
    public String toString() {
        return values.stream()
                .map(v -> new SingleValueBinding(v).toString())
                .collect(Collectors.joining(", ", "(", ")"));
    }
}
