package com.iostate.orca.query.predicate;

public class Not extends Compound {

    private final Predicate predicate;

    public Not(Predicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toString() {
        return "NOT (" + predicate + ")";
    }
}
