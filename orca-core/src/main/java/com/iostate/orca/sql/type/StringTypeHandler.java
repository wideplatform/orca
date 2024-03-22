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
    public String getValue(ResultSet rs, int index, boolean nullable) throws SQLException {
        String value = rs.getString(index);
        if (value == null && !nullable) {
            return "";
        } else {
            return value;
        }
    }

    @Override
    public String getValue(ResultSet rs, String column, boolean nullable) throws SQLException {
        String value = rs.getString(column);
        if (value == null && !nullable) {
            return "";
        } else {
            return value;
        }
    }
}
