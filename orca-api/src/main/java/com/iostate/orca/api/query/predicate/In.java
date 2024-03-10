package com.iostate.orca.api.query.predicate;

import com.iostate.orca.api.query.SqlBuilder;
import com.iostate.orca.api.query.expression.Expression;
import com.iostate.orca.api.query.expression.MultiValueBinding;
import com.iostate.orca.api.query.expression.PathNavigation;

import java.util.Collection;

class In extends AbstractPredicate {

    private final Expression l;
    private final Expression r;

    In(String objectPath, Collection<Object> bindValueCollection) {
        l = new PathNavigation(objectPath);
        r = new MultiValueBinding(bindValueCollection);
    }

    In(Expression l, Expression r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        l.accept(sqlBuilder);
        sqlBuilder.addString(" IN ");
        r.accept(sqlBuilder);
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
