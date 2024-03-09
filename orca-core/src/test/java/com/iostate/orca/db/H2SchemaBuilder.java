package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.sql.SqlHelper;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class H2SchemaBuilder implements SchemaBuilder {

    @Override
    public void build(ConnectionProvider connectionProvider, MetadataManager metadataManager) throws SQLException {
        StringBuilder result = new StringBuilder();
        for (EntityModel entityModel : metadataManager.allEntityModels()) {
            result.append(recreateTableStmt(entityModel));
        }
        String ddl = result.toString();

        new SqlHelper(connectionProvider, null).executeDML(ddl, null);
    }

    private String recreateTableStmt(EntityModel meta) {
        Map<String, String> tablesVsDDLs = TableGeneratorFactory.make(DbType.H2).create(meta);

        return tablesVsDDLs.entrySet().stream()
                .map(entry -> {
                    String table = entry.getKey();
                    String ddl = entry.getValue();
                    return String.format("DROP TABLE IF EXISTS %s;\n%s", table, ddl);
                })
                .collect(Collectors.joining("\n"));
    }
}
