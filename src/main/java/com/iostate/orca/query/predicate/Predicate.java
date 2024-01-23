package com.iostate.orca.query.predicate;

import com.iostate.orca.query.SqlBuilder;

public interface Predicate {

    Predicate and(Predicate other);

    Predicate or(Predicate other);

    Predicate negate();

    void accept(SqlBuilder sqlBuilder);

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
