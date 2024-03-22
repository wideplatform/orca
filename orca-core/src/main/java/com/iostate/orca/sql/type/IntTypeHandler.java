package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntTypeHandler implements TypeHandler<Integer> {
    @Override
    public void setValue(PreparedStatement ps, int index, Integer value) throws SQLException {
        ps.setInt(index, value);
    }

    @Override
    public Integer getValue(ResultSet rs, int index, boolean nullable) throws SQLException {
        int value = rs.getInt(index);
        if (nullable && rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    public Integer getValue(ResultSet rs, String column, boolean nullable) throws SQLException {
        int value = rs.getInt(column);
        if (nullable && rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }
}
