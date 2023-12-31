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
    public Long getValue(ResultSet rs, int index) throws SQLException {
        return rs.getLong(index);
    }

    @Override
    public Long getValue(ResultSet rs, String column) throws SQLException {
        return rs.getLong(column);
    }
}
