package com.iostate.orca.sql;

import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.sql.type.TypeHandlers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Common logic to fully map row to entity
 */
public class EntityResultMapper implements ResultMapper {
    private final EntityModel model;

    public EntityResultMapper(EntityModel model) {
        this.model = model;
    }

    @Override
    public EntityObject mapRow(ResultSet rs) throws SQLException {
        EntityObject entity = model.newInstance();
        entity.persisted(true);

        for (Field field : model.allFields()) {
            if (field.isAssociation()) {
                continue;
            }
            Object value = TypeHandlers.INSTANCE.find(field.getDataType()).getValue(rs, field.getColumnName());
            entity.populateFieldValue(field.getName(), value);
        }

        return entity;
    }
}
