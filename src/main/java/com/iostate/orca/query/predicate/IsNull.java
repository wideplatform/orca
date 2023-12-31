package com.iostate.orca.query.predicate;

class IsNull extends AbstractPredicate {

    private final String attribute;

    public IsNull(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return attribute + " IS NULL";
    }

    @Override
    public Predicate negate() {
        return new IsNotNull(attribute);
    }
}
