package com.iostate.orca.sql.query.condition;

import com.iostate.orca.sql.query.SqlCondition;
import com.iostate.orca.sql.query.SqlExpression;

public class Equal extends SqlCondition {
    public Equal(SqlExpression left, SqlExpression right) {
        super(left, "=", right);
    }
}
