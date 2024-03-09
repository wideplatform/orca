package com.iostate.orca.query.predicate;

import com.iostate.orca.query.SqlBuilder;
import com.iostate.orca.query.expression.Expression;

class IsNull extends AbstractPredicate {

    private final Expression expression;

    public IsNull(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        expression.accept(sqlBuilder);
        sqlBuilder.addString(" IS NULL");
    }

    @Override
    public String toString() {
        return expression + " IS NULL";
    }

    @Override
    public Predicate negate() {
        return new IsNotNull(expression);
    }
}
