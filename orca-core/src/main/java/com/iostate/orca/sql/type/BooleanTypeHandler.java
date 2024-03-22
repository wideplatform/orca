package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanTypeHandler implements TypeHandler<Boolean> {
    @Override
    public void setValue(PreparedStatement ps, int index, Boolean value) throws SQLException {
        ps.setBoolean(index, value);
    }

    @Override
    public Boolean getValue(ResultSet rs, int index, boolean nullable) throws SQLException {
        boolean value = rs.getBoolean(index);
        if (nullable && rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    public Boolean getValue(ResultSet rs, String column, boolean nullable) throws SQLException {
        boolean value = rs.getBoolean(column);
        if (nullable && rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }
}
