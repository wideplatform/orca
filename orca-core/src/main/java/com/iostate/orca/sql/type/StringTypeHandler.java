package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringTypeHandler implements TypeHandler<String> {
    @Override
    public void setValue(PreparedStatement ps, int index, String value) throws SQLException {
        ps.setString(index, value);
    }

    @Override
    public String getValue(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    @Override
    public String getValue(ResultSet rs, String column) throws SQLException {
        return rs.getString(column);
    }
}
