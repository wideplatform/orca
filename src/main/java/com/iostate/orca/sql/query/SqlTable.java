package com.iostate.orca.sql.query;

import com.iostate.orca.sql.query.condition.Equal;

import java.util.ArrayList;
import java.util.List;

public class SqlTable {
    private final String name;
    private final List<String> columns;
    private final List<String> orderColumns;
    private final TableAliasGenerator tableAliasGenerator;
    private final SqlCondition joinCondition;
    private final List<SqlCondition> filters = new ArrayList<>();

    private String alias;

    SqlTable(String name,
             List<String> columns,
             List<String> orderColumns,
             TableAliasGenerator tableAliasGenerator,
             SqlExpression joiner,
             String joinColumn) {
        this.name = name;
        this.columns = columns;
        this.orderColumns = orderColumns;
        this.tableAliasGenerator = tableAliasGenerator;
        this.joinCondition = joiner == null ?
                null :
                new Equal(joiner, new SqlColumnRef(this, joinColumn));
    }

    public void addFilter(SqlCondition condition) {
        filters.add(condition);
    }

    public SqlCondition getJoinCondition() {
        return joinCondition;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        if (alias == null) {
            alias = tableAliasGenerator.generate();
        }
        return alias;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<String> getOrderColumns() {
        return orderColumns;
    }

    public List<SqlCondition> getFilters() {
        return filters;
    }

    public SqlExpression columnRef(String column) {
        return new SqlColumnRef(this, column);
    }

    @Override
    public String toString() {
        return name + " " + alias;
    }
}
