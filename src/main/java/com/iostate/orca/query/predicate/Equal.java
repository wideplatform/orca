package com.iostate.orca.query.predicate;

import com.iostate.orca.query.expression.Attribute;
import com.iostate.orca.query.expression.Expression;
import com.iostate.orca.query.expression.ValueBinding;

class Equal extends AbstractPredicate {

    private final Expression l;
    private final Expression r;

    Equal(String attribute, Object value) {
        l = new Attribute(attribute);
        r = new ValueBinding(value);
    }

    Equal(Expression l, Expression r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public String toString() {
        return l + " = " + r;
    }

    @Override
    public Predicate negate() {
        return new NotEqual(l, r);
    }
}
