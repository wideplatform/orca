package com.iostate.orca.sql;

import com.iostate.orca.api.PersistentObject;
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
    public PersistentObject mapRow(ResultSet rs) throws SQLException {
        PersistentObject po = model.newInstance();
        po.setPersisted(true);

        for (Field field : model.allFields()) {
            if (field.isAssociation()) {
                continue;
            }
            Object value = TypeHandlers.INSTANCE.find(field.getDataType()).getValue(rs, field.getColumnName());
            po.setFieldValue(field.getName(), value);
        }

        return po;
    }
}
