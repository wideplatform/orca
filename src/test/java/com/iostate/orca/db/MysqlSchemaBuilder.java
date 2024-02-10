package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.sql.SqlHelper;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class MysqlSchemaBuilder implements SchemaBuilder {

    @Override
    public void build(ConnectionProvider connectionProvider, MetadataManager metadataManager) throws SQLException {
        SqlHelper sqlHelper = new SqlHelper(connectionProvider, null);
        Iterator<String> stmtIterator = metadataManager.allEntityModels().stream()
                .flatMap(this::recreateTableStmts).iterator();
        while (stmtIterator.hasNext()) {
            sqlHelper.executeDML(stmtIterator.next(), null);
        }
    }

    private Stream<String> recreateTableStmts(EntityModel model) {
        Map<String, String> tablesVsDDLs = TableGeneratorFactory.make(DbType.H2).create(model);

        return tablesVsDDLs.entrySet().stream()
                .flatMap(entry -> {
                    String table = entry.getKey();
                    String ddl = entry.getValue();
                    return Stream.of(
                            "DROP TABLE IF EXISTS " + table,
                            ddl
                    );
                });
    }
}
