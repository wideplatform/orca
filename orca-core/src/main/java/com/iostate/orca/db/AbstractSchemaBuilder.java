package com.iostate.orca.db;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.sql.SqlHelper;

import java.sql.SQLException;

public abstract class AbstractSchemaBuilder implements SchemaBuilder {

    @Override
    public void build(ConnectionProvider connectionProvider, MetadataManager metadataManager) throws SQLException {
        SqlHelper sqlHelper = new SqlHelper(connectionProvider, null);
        TableGenerator tableGenerator = getTableGenerator();
        for (EntityModel entityModel : metadataManager.allEntityModels()) {
            for (String stmt : tableGenerator.drop(entityModel)) {
                sqlHelper.executeDDL(stmt);
            }
            for (String stmt : tableGenerator.create(entityModel)) {
                sqlHelper.executeDDL(stmt);
            }
        }
    }

    protected abstract TableGenerator getTableGenerator();
}
