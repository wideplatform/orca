package com.iostate.orca.metadata;

import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.api.PersistentObject;

public abstract class AbstractField implements Field {
    protected final String name;
    protected final boolean isId;
    protected final boolean isNullable;

    public AbstractField(String name, boolean isId, boolean isNullable) {
        this.name = name;
        this.isId = isId;
        this.isNullable = isNullable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasColumn() {
        return getColumnName() != null;
    }

    @Override
    public boolean isId() {
        return isId;
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public Object getValue(Object entity) {
        try {
            return ((PersistentObject) entity).getFieldValue(getName());
        } catch (Exception e) {
            throw new PersistenceException("Failed to read from field " + getName(), e);
        }
    }

    @Override
    public void setValue(Object entity, Object value) {
        try {
            ((PersistentObject) entity).setFieldValue(getName(), value);
        } catch (Exception e) {
            throw new PersistenceException("Failed to write to field " + getName(), e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s', dataType=%s}", getClass().getSimpleName(), name, getDataType());
    }
}
