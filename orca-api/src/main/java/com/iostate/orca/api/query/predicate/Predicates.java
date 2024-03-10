package com.iostate.orca.api.query.predicate;

import com.iostate.orca.api.query.expression.Expression;
import com.iostate.orca.api.query.expression.PathNavigation;

import java.util.Collection;

public class Predicates {

    public static Predicate equal(String objectPath, Object bindValue) {
        return new Equal(objectPath, bindValue);
    }

    public static Predicate notEqual(String objectPath, Object bindValue) {
        return equal(objectPath, bindValue).negate();
    }

    public static Predicate in(Expression l, Expression r) {
        return new In(l, r);
    }

    public static Predicate in(String objectPath, Collection<Object> bindValueCollection) {
        return new In(objectPath, bindValueCollection);
    }

    public static Predicate notIn(String objectPath, Collection<Object> bindValueCollection) {
        return in(objectPath, bindValueCollection).negate();
    }

    public static Predicate isNull(String objectPath) {
        return new IsNull(new PathNavigation(objectPath));
    }

    public static Predicate isNotNull(String objectPath) {
        return isNull(objectPath).negate();
    }

}
