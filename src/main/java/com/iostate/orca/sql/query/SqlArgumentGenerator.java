package com.iostate.orca.sql.query;

import java.util.ArrayList;
import java.util.List;

class SqlArgumentGenerator {

    private int ordinal;
    private final List<SqlArgument> arguments = new ArrayList<>();

    SqlExpression generate(Object value) {
        return new SqlArgument(value, this);
    }

    // When concatenating SQL and evaluating each argument,
    // it is called by the argument and tells the argument its ordinal
    int register(SqlArgument argument) {
        ordinal++;
        arguments.add(argument);
        return ordinal;
    }

    public List<SqlArgument> getArguments() {
        return arguments;
    }
}
