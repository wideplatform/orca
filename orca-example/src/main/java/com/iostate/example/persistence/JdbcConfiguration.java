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

        Properties prop = readProperties("orca-db.properties");
        dataSource.setDriverClassName(prop.getProperty("database.driver"));
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
