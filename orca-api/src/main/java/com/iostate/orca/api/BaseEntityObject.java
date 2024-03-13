package com.iostate.orca.api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseEntityObject implements EntityObject {

    private boolean persisted;
    private final Map<String, Object> _foreignKeyValues = new ConcurrentHashMap<>();
    private final Set<String> _updatedFields = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isPersisted() {
        return persisted;
    }

    @Override
    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    @Override
    public Object getForeignKeyValue(String name) {
        return _foreignKeyValues.get(name);
    }

    @Override
    public void setForeignKeyValue(String name, Object value) {
        _foreignKeyValues.put(name, value);
    }

    @Override
    public Set<String> get_updatedFields() {
        return _updatedFields;
    }

    protected void markUpdatedField(String name) {
        _updatedFields.add(name);
    }
}
