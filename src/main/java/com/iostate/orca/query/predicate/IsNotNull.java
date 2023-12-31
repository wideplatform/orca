package com.iostate.orca.query.predicate;

class IsNotNull extends AbstractPredicate {

    private final String attribute;

    public IsNotNull(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return attribute + " IS NOT NULL";
    }

    @Override
    public Predicate negate() {
        return new IsNull(attribute);
    }
}
