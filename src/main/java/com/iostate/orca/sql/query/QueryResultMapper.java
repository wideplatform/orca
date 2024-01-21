package com.iostate.orca.sql.query;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.sql.ResultMapper;
import com.iostate.orca.sql.type.TypeHandlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Common logic to fully map row to entity
 */
class QueryResultMapper implements ResultMapper {
    private final EntityModel model;
    private final List<SelectedField> selectedFields;

    QueryResultMapper(EntityModel model, List<SelectedField> selectedFields) {
        this.model = model;
        this.selectedFields = selectedFields;
    }

    @Override
    public PersistentObject mapRow(ResultSet rs) throws SQLException {
        PersistentObject po = model.newInstance();
        po.setPersisted(true);

        for (SelectedField sf : selectedFields) {
            Field field = sf.getField();
            Object value = TypeHandlers.INSTANCE.find(field.getDataType())
                    .getValue(rs, sf.getIndex());
            po.setFieldValue(field.getName(), value);
        }

        return po;
    }
}
