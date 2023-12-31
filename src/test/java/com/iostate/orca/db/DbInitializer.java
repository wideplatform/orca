package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.metadata.MetadataManager;

import java.io.IOException;
import java.sql.SQLException;

public class DbInitializer {

    private MetadataManager metadataManager;
    private ConnectionProvider connectionProvider;

    public DbInitializer(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    public void execute() throws IOException, SQLException {
        String dbType = System.getProperty("mdp.db.type", "h2");
        connectionProvider = DataSourceWrapper.of(DataSourceUtil.create(dbType));
        SchemaBuilderFactory.make(DbType.of(dbType)).build(connectionProvider, metadataManager);
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }
}
