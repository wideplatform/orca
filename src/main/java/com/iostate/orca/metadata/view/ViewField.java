package com.iostate.orca.metadata.view;

import com.iostate.orca.metadata.DataType;

public class ViewField {

    private String name;
    // If aliased, present with original name of the entity field, else null.
    private String originalName;

    private DataType dataType;

    protected ViewField() {
    }

    public ViewField(String name, String originalName, DataType dataType) {
        this.name = name;
        this.originalName = originalName;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public DataType getDataType() {
        return dataType;
    }
}
