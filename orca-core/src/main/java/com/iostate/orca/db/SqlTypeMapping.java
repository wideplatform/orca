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

    private static final Map<DataType, String> commonMappings = new HashMap<>();
    private static final Map<DataType, String> mysqlMappings = new HashMap<>();
    private static final Map<DataType, String> postgresqlMappings = new HashMap<>();

    static {
        commonMappings.put(SimpleDataType.BOOLEAN, "BOOLEAN");
        commonMappings.put(SimpleDataType.INT, "INTEGER");
        commonMappings.put(SimpleDataType.LONG, "BIGINT");
        commonMappings.put(SimpleDataType.DECIMAL, "DECIMAL(18,8)");
        commonMappings.put(SimpleDataType.STRING, "VARCHAR(255)");
        commonMappings.put(SimpleDataType.DATE, "DATE");
    }
    static {
        mysqlMappings.put(SimpleDataType.DATETIME, "DATETIME(6)");
    }
    static {
        postgresqlMappings.put(SimpleDataType.DATETIME, "TIMESTAMP");
    }

    static String sqlType(DbType dbType, Field field) {
        if (field.isAssociation()) {
            AssociationField rf = (AssociationField) field;
            if (rf.hasColumn()) {
                return map(dbType, rf.getTargetModelRef().model().getIdField().getDataType());
            } else {
                throw new IllegalArgumentException("field " + rf + " should not map to SQL type");
            }
        }

        return map(dbType, field.getDataType());
    }

    static String map(DbType dbType, DataType dataType) {
        String result = null;
        switch (dbType) {
            case H2, MYSQL -> result = mysqlMappings.get(dataType);
            case POSTGRESQL -> result = postgresqlMappings.get(dataType);
        }
        if (result != null) {
            return result;
        }
        return commonMappings.get(dataType);
    }
}
