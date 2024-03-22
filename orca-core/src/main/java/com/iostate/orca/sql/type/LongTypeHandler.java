package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongTypeHandler implements TypeHandler<Long> {
    @Override
    public void setValue(PreparedStatement ps, int index, Long value) throws SQLException {
        ps.setLong(index, value);
    }

    @Override
    public Long getValue(ResultSet rs, int index, boolean nullable) throws SQLException {
        long value = rs.getLong(index);
        if (nullable && rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    public Long getValue(ResultSet rs, String column, boolean nullable) throws SQLException {
        long value = rs.getLong(column);
        if (nullable && rs.wasNull()) {
            return null;
        } else {
            return value;
        }
    }
}
