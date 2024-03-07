package com.iostate.orca.metadata;

public class MiddleTableImage {
    private final String tableName;
    private final String sourceIdColumn;
    private final String targetIdColumn;

    MiddleTableImage(HasAndBelongsToMany mm) {
        if (mm.getMappedByFieldName() != null) {
            MiddleTable middleTable = ((HasAndBelongsToMany) mm.getMappedByField()).getMiddleTable();
            tableName = middleTable.getTableName();
            sourceIdColumn = middleTable.getTargetIdColumnName();
            targetIdColumn = middleTable.getSourceIdColumnName();
        } else {
            MiddleTable middleTable = mm.getMiddleTable();
            tableName = middleTable.getTableName();
            sourceIdColumn = middleTable.getSourceIdColumnName();
            targetIdColumn = middleTable.getTargetIdColumnName();
        }
    }

    public String getTableName() {
        return tableName;
    }

    public String getSourceIdColumn() {
        return sourceIdColumn;
    }

    public String getTargetIdColumn() {
        return targetIdColumn;
    }
}
