package com.iostate.orca.db;

import com.iostate.orca.util.PropertiesReader;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Mocks a data source with real connection for test purpose
 */
public class DataSourceUtil {

    private DataSourceUtil() {
    }

    public static DataSource create(DbType dbType) throws IOException {
        Properties prop = new PropertiesReader().read(String.format("db-%s.properties", dbType.toString().toLowerCase()));
        String url = prop.getProperty("database.url");
        String username = prop.getProperty("database.username");
        String password = prop.getProperty("database.password");

        return (DataSource) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{DataSource.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getConnection")) {
                        return DriverManager.getConnection(url, username, password);
                    }
                    throw new UnsupportedOperationException();
                });
    }
}
