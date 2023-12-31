package com.iostate.orca.query.predicate;

import com.iostate.orca.query.expression.Attribute;
import com.iostate.orca.query.expression.CollectionBinding;
import com.iostate.orca.query.expression.Expression;

import java.util.Collection;

class In extends AbstractPredicate {

    private final Expression l;
    private final Expression r;

    In(String attribute, Collection<Object> bindValueCollection) {
        l = new Attribute(attribute);
        r = new CollectionBinding(bindValueCollection);
    }

    In(Expression l, Expression r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public String toString() {
        return l + " IN " + r;
    }

    @Override
    public Predicate negate() {
        return new NotIn(l, r);
    }
}
