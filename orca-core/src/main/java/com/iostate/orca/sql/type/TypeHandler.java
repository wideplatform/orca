package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandler<T> {

    void setValue(PreparedStatement ps, int index, T value) throws SQLException;

    T getValue(ResultSet rs, int index, boolean nullable) throws SQLException;

    T getValue(ResultSet rs, String column, boolean nullable) throws SQLException;
}
