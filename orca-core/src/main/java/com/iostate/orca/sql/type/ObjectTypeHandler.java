package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectTypeHandler implements TypeHandler<Object> {
    @Override
    public void setValue(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setObject(index, value);
    }

    @Override
    public Object getValue(ResultSet rs, int index, boolean nullable) throws SQLException {
        return rs.getObject(index);
    }

    @Override
    public Object getValue(ResultSet rs, String column, boolean nullable) throws SQLException {
        return rs.getObject(column);
    }
}
