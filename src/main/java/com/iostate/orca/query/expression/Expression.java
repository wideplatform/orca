package com.iostate.orca.query.expression;

import com.iostate.orca.query.SqlBuilder;

public interface Expression {
    void accept(SqlBuilder sqlBuilder);
}
