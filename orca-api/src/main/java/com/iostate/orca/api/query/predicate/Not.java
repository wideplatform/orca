package com.iostate.orca.api.query.predicate;

import com.iostate.orca.api.query.SqlBuilder;

public class Not extends Compound {

    private final Predicate predicate;

    public Not(Predicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public void accept(SqlBuilder sqlBuilder) {
        sqlBuilder.addString("NOT (");
        predicate.accept(sqlBuilder);
        sqlBuilder.addString(")");
    }

    @Override
    public String toString() {
        return "NOT (" + predicate + ")";
    }
}
