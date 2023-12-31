package com.iostate.orca.query.predicate;

public abstract class Compound extends AbstractPredicate {

    @Override
    public Predicate negate() {
        return new Not(this);
    }
}
