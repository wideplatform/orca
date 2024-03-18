package com.iostate.orca.api;

import java.sql.Connection;

/**
 * SPI for providing DB connections to Orca framework
 */
public interface ConnectionProvider {
    Connection getConnection();
}
