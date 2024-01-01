package com.iostate.orca.metadata;

import com.iostate.orca.metadata.dto.FieldDto;

public class SimpleField extends AbstractField {

    private final String columnName;
    private final DataType dataType;

    public SimpleField(String name, String columnName, DataType dataType,
                       boolean isId, boolean isNullable) {
        super(name, isId, isNullable);
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean isAssociation() {
        return false;
    }

    @Override
    public final FieldDto toDto() {
        FieldDto dto = new FieldDto();
        dto.setName(getName());
        dto.setColumnName(getColumnName());
        dto.setDataTypeName(getDataType().name());
        dto.setNullable(isNullable());
        return dto;
    }
}
