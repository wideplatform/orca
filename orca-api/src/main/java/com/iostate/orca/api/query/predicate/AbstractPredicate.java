package com.iostate.orca.api.query.predicate;

public abstract class AbstractPredicate implements Predicate {

    @Override
    public Predicate and(Predicate other) {
        return new And(this, other);
    }

    @Override
    public Predicate or(Predicate other) {
        return new Or(this, other);
    }
}
