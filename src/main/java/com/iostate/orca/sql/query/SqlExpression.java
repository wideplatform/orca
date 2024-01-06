package com.iostate.orca.sql.query;

public interface SqlExpression {

}

class SqlColumnRef implements SqlExpression {
    private final SqlTable table;
    private final String column;

    SqlColumnRef(SqlTable table, String column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public String toString() {
        return table.getAlias() + "." + column;
    }
}

class SqlArgument implements SqlExpression {
    private final Object value;
    private int ordinal = -1;
    private final SqlArgumentGenerator generator;

    SqlArgument(Object value, SqlArgumentGenerator generator) {
        this.value = value;
        this.generator = generator;
    }

    public int getOrdinal() {
        if (ordinal < 0) {
            ordinal = generator.register(this);
        }
        return ordinal;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        getOrdinal();
        return "?";
    }
}
