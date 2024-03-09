package com.iostate.orca.metadata.dto;

import java.util.List;

public class FieldDto {
    private String name;
    private String columnName;
    private String dataTypeName;
    private boolean isNullable;
    // Association-only
    private String associationType;
    private String targetModelName;
    private String mappedByFieldName;
    private String fetchType;
    private List<String> cascadeTypes = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public String getAssociationType() {
        return associationType;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    public String getTargetModelName() {
        return targetModelName;
    }

    public void setTargetModelName(String targetModelName) {
        this.targetModelName = targetModelName;
    }

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public List<String> getCascadeTypes() {
        return cascadeTypes;
    }

    public void setCascadeTypes(List<String> cascadeTypes) {
        this.cascadeTypes = cascadeTypes;
    }

    public String getMappedByFieldName() {
        return mappedByFieldName;
    }

    public void setMappedByFieldName(String mappedByFieldName) {
        this.mappedByFieldName = mappedByFieldName;
    }
}
