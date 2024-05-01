package com.iostate.orca.metadata.view;

import com.iostate.orca.metadata.DataType;
import com.iostate.orca.metadata.IField;
import com.iostate.orca.metadata.dto.ViewFieldDto;

// Always eager, never lazy.
public class ViewField implements IField {
    private final String name;
    // If aliased, it is the mapped entity field name, else is null.
    private final String originalName;

    private final DataType dataType;
    private final boolean isId;
    private final boolean isNullable;

    public ViewField(String name, String originalName, DataType dataType, boolean isId, boolean isNullable) {
        this.name = name;
        this.originalName = originalName;
        this.dataType = dataType;
        this.isId = isId;
        this.isNullable = isNullable;
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

    public boolean isId() {
        return isId;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public ViewFieldDto toDto() {
        ViewFieldDto dto = new ViewFieldDto();
        dto.setName(getName());
        dto.setOriginalName(getOriginalName());
        dto.setDataTypeName(getDataType().name());
        dto.setNullable(isNullable());
        return dto;
    }
}
