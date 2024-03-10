package com.iostate.orca.api.query.predicate;

import com.iostate.orca.api.query.SqlBuilder;
import com.iostate.orca.api.query.expression.Expression;

class NotEqual extends AbstractPredicate {

    private final Expression l;
    private final Expression r;

    public NotEqual(Expression l, Expression r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        l.accept(sqlBuilder);
        sqlBuilder.addString(" <> ");
        r.accept(sqlBuilder);
    }

    @Override
    public String toString() {
        return l + " <> " + r;
    }

    @Override
    public Predicate negate() {
        return new Equal(l, r);
    }
}
