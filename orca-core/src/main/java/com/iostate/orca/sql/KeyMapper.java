package com.iostate.orca.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface KeyMapper {
    Object mapKey(ResultSet keySet) throws SQLException;
}
