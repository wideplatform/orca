package com.iostate.orca.sql;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.sql.type.TypeHandlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Common logic to fully map row to entity
 */
public class QueryResultMapper implements ResultMapper {
    private final EntityModel model;
    private final List<Field> selectedFields;

    public QueryResultMapper(EntityModel model, List<Field> selectedFields) {
        this.model = model;
        this.selectedFields = selectedFields;
    }

    @Override
    public PersistentObject mapRow(ResultSet rs) throws SQLException {
        PersistentObject po = model.newInstance();
        po.setPersisted(true);

        for (Field field : selectedFields) {
            if (field.isAssociation()) {
                AssociationField af = (AssociationField) field;
                if (af.hasColumn() && af.getFetchType() == FetchType.EAGER) {
                    // retrieve value
                }
            } else {
                Object value = TypeHandlers.INSTANCE.find(field.getDataType()).getValue(rs, field.getColumnName());
                po.setFieldValue(field.getName(), value);
            }
        }

        return po;
    }
}
