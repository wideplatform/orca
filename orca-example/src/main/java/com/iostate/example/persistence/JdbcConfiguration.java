package com.iostate.example.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

@Configuration
public class JdbcConfiguration {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        String dbType = System.getProperty("mdp.db.type", "h2");
        Properties prop = readProperties(String.format("db-%s.properties", dbType));
        if ("h2".equals(dbType)) {
            dataSource.setDriverClassName("org.h2.Driver");
        } else if ("mysql".equals(dbType)) {
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        } else {
            throw new IllegalArgumentException(dbType);
        }

        dataSource.setUrl(prop.getProperty("database.url"));
        dataSource.setUsername(prop.getProperty("database.username"));
        dataSource.setPassword(prop.getProperty("database.password"));
        return dataSource;
    }

    private Properties readProperties(String path) {
        Properties prop = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            prop.load(in);
            return prop;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
