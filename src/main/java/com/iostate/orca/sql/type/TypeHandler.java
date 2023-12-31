package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandler<T> {

    void setValue(PreparedStatement ps, int index, T value) throws SQLException;

    T getValue(ResultSet rs, int index) throws SQLException;

    T getValue(ResultSet rs, String column) throws SQLException;
}
