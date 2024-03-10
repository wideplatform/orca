package com.iostate.orca.api.query.expression;

import com.iostate.orca.api.query.SqlBuilder;

public interface Expression {
    void accept(SqlBuilder sqlBuilder);
}
