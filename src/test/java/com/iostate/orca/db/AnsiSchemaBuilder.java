package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.sql.SqlHelper;

import java.sql.SQLException;
import java.util.Map;

public class AnsiSchemaBuilder implements SchemaBuilder {
    @Override
    public void build(ConnectionProvider connectionProvider, MetadataManager metadataManager) throws SQLException {
        String schema = connectionProvider.getConnection().getSchema();

        SqlHelper sqlHelper = new SqlHelper(connectionProvider, null);

        for (EntityModel entityModel : metadataManager.allEntityModels()) {
            Map<String, String> tablesVsDDLs = TableGeneratorFactory.make(DbType.ANSI).create(entityModel);

            for (Map.Entry<String, String> entry : tablesVsDDLs.entrySet()) {
                String table = entry.getKey();
                String ddl = entry.getValue();
                recreateTable(sqlHelper, schema, table, ddl);
            }
        }
    }

    private void recreateTable(SqlHelper sqlHelper, String schema, String table, String ddl) throws SQLException {
        long count = sqlHelper.executeCount(
                "SELECT COUNT(*) FROM PUBLIC.TABLES WHERE SCHEMA_NAME = ? AND TABLE_NAME = ? LIMIT 1",
                new Object[]{schema, table});
        if (count > 0) {
            sqlHelper.executeDML(String.format("DROP TABLE %s.%s", schema, table), new Object[]{});
        }
        sqlHelper.executeDML(ddl, null);
    }
}
