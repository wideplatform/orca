package com.iostate.orca.query.expression;

public class ValueBinding implements Expression {

    private final Object value;

    public ValueBinding(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (value instanceof CharSequence) {
            return "'" + value + "'";
        }
        return String.valueOf(value);
    }
}
