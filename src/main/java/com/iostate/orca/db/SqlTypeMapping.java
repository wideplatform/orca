package com.iostate.orca.db;

import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.DataType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.SimpleDataType;

import java.util.HashMap;
import java.util.Map;


/**
 * Mapping: javaType -- sqlType
 */
class SqlTypeMapping {

    // 36 with 4 '-' dashes, or 32 without '-' dashes
    private static final String UUID_TYPE = "CHAR(36)";

    private static final Map<DataType, String> sqlTypes = new HashMap<>();

    static {
        sqlTypes.put(SimpleDataType.STRING, "NVARCHAR(255)");
        sqlTypes.put(SimpleDataType.LONG, "NUMBER");
        sqlTypes.put(SimpleDataType.INT, "NUMBER");
        sqlTypes.put(SimpleDataType.DATETIME, "DATETIME");
        sqlTypes.put(SimpleDataType.DATE, "DATE");
        sqlTypes.put(SimpleDataType.BOOLEAN, "BOOLEAN");
    }

    static String sqlType(Field field) {
        if (field.isAssociation()) {
            AssociationField rf = (AssociationField) field;
            if (rf.hasColumn()) {
                return sqlTypes.get(rf.getTargetModel().model().getIdField().getDataType());
            } else {
                throw new IllegalArgumentException("field " + rf + " should not map to SQL type");
            }
        }

        return sqlTypes.get(field.getDataType());
    }
}
