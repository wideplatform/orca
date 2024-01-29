package com.iostate.orca.api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasePO implements PersistentObject {

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
    public Object getForeignKeyValue(String key) {
        return _foreignKeyValues.get(key);
    }

    @Override
    public void setForeignKeyValue(String key, Object value) {
        _foreignKeyValues.put(key, value);
    }

    @Override
    public Set<String> get_updatedFields() {
        return _updatedFields;
    }

    protected void markUpdatedField(String name) {
        _updatedFields.add(name);
    }
}
