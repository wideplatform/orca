package com.iostate.orca.metadata.dto;

import java.util.ArrayList;
import java.util.List;

public class ViewModelDto {
    private String name;
    private String entityModelName;
    private ViewFieldDto idField;
    private List<ViewFieldDto> dataFields = new ArrayList<>();
    private String linkedClassName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityModelName() {
        return entityModelName;
    }

    public void setEntityModelName(String entityModelName) {
        this.entityModelName = entityModelName;
    }

    public ViewFieldDto getIdField() {
        return idField;
    }

    public void setIdField(ViewFieldDto idField) {
        this.idField = idField;
    }

    public List<ViewFieldDto> getDataFields() {
        return dataFields;
    }

    public void setDataFields(List<ViewFieldDto> dataFields) {
        this.dataFields = dataFields;
    }

    public String getLinkedClassName() {
        return linkedClassName;
    }

    public void setLinkedClassName(String linkedClassName) {
        this.linkedClassName = linkedClassName;
    }
}
