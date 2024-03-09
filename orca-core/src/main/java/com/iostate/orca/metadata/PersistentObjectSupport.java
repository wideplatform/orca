package com.iostate.orca.metadata;

import com.iostate.orca.api.PersistentObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An alternative. Extend it when you can't use the interface and weaver
 */
public abstract class PersistentObjectSupport implements PersistentObject {

    private boolean persisted;
    private final Map<String, Object> valueMap = new HashMap<>();

    @Override
    public boolean isPersisted() {
        return persisted;
    }

    @Override
    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    @Override
    public Object getFieldValue(String name) {
        return valueMap.get(name);
    }

    @Override
    public void setFieldValue(String name, Object value) {
        valueMap.put(name, value);
    }
}
