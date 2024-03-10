package com.iostate.orca.api.query.predicate;

import com.iostate.orca.api.query.SqlBuilder;
import com.iostate.orca.api.query.expression.Expression;
import com.iostate.orca.api.query.expression.PathNavigation;
import com.iostate.orca.api.query.expression.SingleValueBinding;

class Equal extends AbstractPredicate {

    private final Expression l;
    private final Expression r;

    Equal(String objectPath, Object value) {
        l = new PathNavigation(objectPath);
        r = new SingleValueBinding(value);
    }

    Equal(Expression l, Expression r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        l.accept(sqlBuilder);
        sqlBuilder.addString(" = ");
        r.accept(sqlBuilder);
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
