package com.iostate.orca.sql.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlQuery {

    // The first table is the driving table with null join condition
    // The rest tables are join tables each with a join condition
    private final List<SqlTable> tables = new ArrayList<>();
    private final TableAliasGenerator tableAliasGenerator = new TableAliasGenerator();
    private final SqlArgumentGenerator sqlArgumentGenerator = new SqlArgumentGenerator();

    public SqlTable addDrivingTable(String name,
                                    List<String> columns,
                                    List<String> orderColumns) {
        return addJoinTable(name, columns, orderColumns, null, null);
    }

    public SqlTable addJoinTable(String name,
                                 List<String> columns,
                                 List<String> orderColumns,
                                 SqlExpression joiner,
                                 String joinColumn) {
        SqlTable table = new SqlTable(name, columns, orderColumns, tableAliasGenerator, joiner, joinColumn);
        tables.add(table);
        return table;
    }

    public SqlExpression createArgument(Object value) {
        return sqlArgumentGenerator.generate(value);
    }

    public List<Object> getArgumentValues() {
        return sqlArgumentGenerator.getArguments()
                .stream()
                .map(SqlArgument::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        if (tables.isEmpty()) {
            throw new IllegalStateException("no table specified");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        {
            int totalColumnCount = 0;
            for (SqlTable table : tables) {
                for (String column : table.getColumns()) {
                    if (totalColumnCount > 0) {
                        sb.append(",");
                    }
                    sb.append(table.getAlias()).append('.').append(column);
                    totalColumnCount++;
                }
            }
        }

        sb.append(" FROM ");
        for (SqlTable table : tables) {
            if (table.getJoinCondition() == null) {
                sb.append(table);
            } else {
                sb.append(" LEFT JOIN ").append(table).append(" ON ").append(table.getJoinCondition());
            }
        }

        if (tables.stream().anyMatch(t -> !t.getFilters().isEmpty())) {
            sb.append(" WHERE ");
            int totalFilterCount = 0;
            for (SqlTable table : tables) {
                for (SqlCondition filter : table.getFilters()) {
                    if (totalFilterCount > 0) {
                        sb.append(" AND ");
                    }
                    sb.append(filter);
                    totalFilterCount++;
                }
            }
        }

        if (tables.stream().anyMatch(t -> !t.getOrderColumns().isEmpty())) {
            sb.append(" ORDER BY ");
            int totalOrderCount = 0;
            for (SqlTable table : tables) {
                for (String column : table.getOrderColumns()) {
                    if (totalOrderCount > 0) {
                        sb.append(",");
                    }
                    sb.append(table.getAlias()).append('.').append(column);
                    totalOrderCount++;
                }
            }
        }

        return sb.toString();
    }
}
