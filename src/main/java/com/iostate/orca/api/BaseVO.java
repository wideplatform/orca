package com.iostate.orca.api;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseVO implements TrackedObject {

    private final Set<String> _updatedFields = ConcurrentHashMap.newKeySet();

    @Override
    public Set<String> get_updatedFields() {
        return _updatedFields;
    }

    protected void markUpdatedField(String name) {
        _updatedFields.add(name);
    }
}
