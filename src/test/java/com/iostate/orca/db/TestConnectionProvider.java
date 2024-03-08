package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnectionProvider implements ConnectionProvider {

    private final DataSource dataSource;
    private Connection connection;

    private TestConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static TestConnectionProvider of(DataSource dataSource) {
        return new TestConnectionProvider(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
