package com.iostate.orca.query.predicate;

import com.iostate.orca.query.expression.Expression;

import java.util.Collection;

public class Predicates {

    public static Predicate equal(String attribute, Object bindValue) {
        return new Equal(attribute, bindValue);
    }

    public static Predicate notEqual(String attribute, Object bindValue) {
        return equal(attribute, bindValue).negate();
    }

    public static Predicate in(Expression l, Expression r) {
        return new In(l, r);
    }

    public static Predicate in(String attribute, Collection<Object> bindValueCollection) {
        return new In(attribute, bindValueCollection);
    }

    public static Predicate notIn(String attribute, Collection<Object> bindValueCollection) {
        return in(attribute, bindValueCollection).negate();
    }

    public static Predicate isNull(String attribute) {
        return new IsNull(attribute);
    }

    public static Predicate isNotNull(String attribute) {
        return isNull(attribute).negate();
    }

}
