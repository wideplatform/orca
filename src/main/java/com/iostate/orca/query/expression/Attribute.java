package com.iostate.orca.query.expression;

public class Attribute implements Expression {

    private final String name;

    public Attribute(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
