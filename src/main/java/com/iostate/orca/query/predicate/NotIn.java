package com.iostate.orca.query.predicate;

import com.iostate.orca.query.SqlBuilder;
import com.iostate.orca.query.expression.Expression;

class NotIn extends AbstractPredicate {

    private final Expression l;
    private final Expression r;

    public NotIn(Expression l, Expression r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        l.accept(sqlBuilder);
        sqlBuilder.addString(" NOT IN ");
        r.accept(sqlBuilder);
    }

    @Override
    public String toString() {
        return l + " NOT IN " + r;
    }

    @Override
    public Predicate negate() {
        return new In(l, r);
    }
}
