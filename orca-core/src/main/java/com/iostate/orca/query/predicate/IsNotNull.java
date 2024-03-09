package com.iostate.orca.query.predicate;

import com.iostate.orca.query.SqlBuilder;
import com.iostate.orca.query.expression.Expression;

class IsNotNull extends AbstractPredicate {

    private final Expression expression;

    public IsNotNull(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        expression.accept(sqlBuilder);
        sqlBuilder.addString(" IS NOT NULL");
    }

    @Override
    public String toString() {
        return expression + " IS NOT NULL";
    }

    @Override
    public Predicate negate() {
        return new IsNull(expression);
    }
}
