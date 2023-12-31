package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceWrapper implements ConnectionProvider {

    private final DataSource dataSource;

    private DataSourceWrapper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static DataSourceWrapper of(DataSource dataSource) {
        return new DataSourceWrapper(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
