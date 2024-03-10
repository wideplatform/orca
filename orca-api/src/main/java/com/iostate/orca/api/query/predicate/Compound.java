package com.iostate.orca.api.query.predicate;

public abstract class Compound extends AbstractPredicate {

    @Override
    public Predicate negate() {
        return new Not(this);
    }
}
