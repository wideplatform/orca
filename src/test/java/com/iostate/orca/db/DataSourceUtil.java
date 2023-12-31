package com.iostate.orca.db;

import com.iostate.orca.util.PropertiesReader;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Mocks a data source with real connection for test purpose
 */
public class DataSourceUtil {

    private DataSourceUtil() {
    }

    public static DataSource create(String dbType) throws SQLException, IOException {
        Properties prop = new PropertiesReader().read(String.format("db-%s.properties", dbType));
        Connection connection = DriverManager.getConnection(
                prop.getProperty("database.url"),
                prop.getProperty("database.userName"),
                prop.getProperty("database.password"));

        String schema = prop.getProperty("database.schema");
        if (schema != null && !schema.isEmpty()) {
            connection.setSchema(schema);
        }

        return (DataSource) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{DataSource.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getConnection")) {
                        return connection;
                    }
                    throw new UnsupportedOperationException();
                });
    }
}
