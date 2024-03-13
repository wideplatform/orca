package com.iostate.orca.metadata;

import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.exception.PersistenceException;

import java.util.Objects;

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
    public Object getValue(EntityObject entity) {
        Objects.requireNonNull(entity, "entity is null when reading from field " + getName());
        try {
            return entity.getFieldValue(getName());
        } catch (Exception e) {
            throw new PersistenceException("Failed to read from field " + getName(), e);
        }
    }

    @Override
    public void setValue(EntityObject entity, Object value) {
        Objects.requireNonNull(entity, "entity is null when writing to field " + getName());
        try {
            entity.setFieldValue(getName(), value);
        } catch (Exception e) {
            throw new PersistenceException("Failed to write to field " + getName(), e);
        }
    }

    @Override
    public boolean isUpdated(EntityObject entity) {
        return entity.get_updatedFields().contains(name);
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s', dataType=%s}", getClass().getSimpleName(), name, getDataType());
    }
}
