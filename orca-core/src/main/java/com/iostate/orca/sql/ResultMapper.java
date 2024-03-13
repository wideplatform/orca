package com.iostate.orca.sql;

import com.iostate.orca.api.EntityObject;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultMapper {
    EntityObject mapRow(ResultSet rs) throws SQLException;
}