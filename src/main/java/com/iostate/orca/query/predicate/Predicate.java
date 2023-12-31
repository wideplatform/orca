package com.iostate.orca.query.predicate;

public interface Predicate {

    Predicate and(Predicate other);

    Predicate or(Predicate other);

    Predicate negate();

    static Predicate and(Predicate... predicates) {
        Predicate result = null;
        for (Predicate each : predicates) {
            if (result == null) {
                result = each;
            } else {
                result = result.and(each);
            }
        }
        return result;
    }

    static Predicate and(Predicate first, Predicate... predicates) {
        Predicate result = first;
        for (Predicate each : predicates) {
            result = result.and(each);
        }
        return result;
    }
}
