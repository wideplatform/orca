package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.metadata.MetadataManager;

import java.sql.SQLException;

/**
 * Builds schema into DB for test purpose
 */
public interface SchemaBuilder {
    void build(ConnectionProvider connectionProvider, MetadataManager metadataManager) throws SQLException;
}
