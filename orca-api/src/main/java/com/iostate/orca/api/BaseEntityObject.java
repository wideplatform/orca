package com.iostate.orca.api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A base for every entity object
 */
public abstract class BaseEntityObject implements EntityObject {

    private boolean persisted;
    private boolean loading;
    private final Map<String, Object> _foreignKeyValues = new ConcurrentHashMap<>();
    private final Set<String> _updatedFields = ConcurrentHashMap.newKeySet();

    @Override
    public boolean persisted() {
        return persisted;
    }

    @Override
    public void persisted(boolean persisted) {
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
    public void populateFieldValue(String name, Object value) {
        loading = true;
        setFieldValue(name, value);
        loading = false;
    }

    @Override
    public Set<String> get_updatedFields() {
        return _updatedFields;
    }

    protected void markUpdatedField(String name) {
        if (!loading) {
            _updatedFields.add(name);
        }
    }
}
