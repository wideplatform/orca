package com.iostate.orca.metadata;

public class SimpleField extends AbstractField {

    private final String columnName;
    private final Class<?> declaredType;

    public SimpleField(String name, String columnName, Class<?> declaredType,
                       boolean isId, boolean isNullable) {
        super(name, isId, isNullable);
        this.columnName = columnName;
        this.declaredType = declaredType;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class<?> getDeclaredType() {
        return declaredType;
    }

    @Override
    public DataType getDataType() {
        return SimpleDataType.valueOf(getDeclaredType());
    }

    @Override
    public boolean isAssociation() {
        return false;
    }
}
