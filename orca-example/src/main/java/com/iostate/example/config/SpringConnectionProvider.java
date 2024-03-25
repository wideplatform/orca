package com.iostate.example.config;

import com.iostate.orca.api.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class SpringConnectionProvider implements ConnectionProvider {
    @Autowired
    private DataSource dataSource;

    @Override
    public Connection getConnection() {
        // This line gets the thread-bound connection in current transaction
        return DataSourceUtils.getConnection(dataSource);
    }
}
