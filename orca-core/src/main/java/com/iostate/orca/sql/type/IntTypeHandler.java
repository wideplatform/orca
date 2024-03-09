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
    public Integer getValue(ResultSet rs, int index) throws SQLException {
        return rs.getInt(index);
    }

    @Override
    public Integer getValue(ResultSet rs, String column) throws SQLException {
        return rs.getInt(column);
    }
}
