package com.iostate.orca.sql;

import com.iostate.orca.api.PersistentObject;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultMapper {
    PersistentObject mapRow(ResultSet rs) throws SQLException;
}