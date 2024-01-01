package com.iostate.orca.metadata.dto;

import java.util.ArrayList;
import java.util.List;

public class EntityModelDto {
    private String name;
    private String tableName;
    private String idGenerator;
    private FieldDto idField;
    private List<FieldDto> dataFields = new ArrayList<>();
    private String linkedClassName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(String idGenerator) {
        this.idGenerator = idGenerator;
    }

    public FieldDto getIdField() {
        return idField;
    }

    public void setIdField(FieldDto idField) {
        this.idField = idField;
    }

    public List<FieldDto> getDataFields() {
        return dataFields;
    }

    public void setDataFields(List<FieldDto> dataFields) {
        this.dataFields = dataFields;
    }

    public String getLinkedClassName() {
        return linkedClassName;
    }

    public void setLinkedClassName(String linkedClassName) {
        this.linkedClassName = linkedClassName;
    }
}
