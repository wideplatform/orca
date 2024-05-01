package com.iostate.orca.core;

import com.iostate.orca.api.BaseViewObject;
import com.iostate.orca.metadata.view.ViewModel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation for no-code development
 */
public class CommonViewObject extends BaseViewObject {
    private final ViewModel model;
    private final Map<String, Object> valueMap = new ConcurrentHashMap<>();

    public CommonViewObject(ViewModel model) {
        this.model = model;
    }

    public String getModelName() {
        return model.getName();
    }

    @Override
    public Object getFieldValue(String name) {
        Objects.requireNonNull(name, "field name must not be null");
        return valueMap.get(name);
    }

    @Override
    public synchronized void setFieldValue(String name, Object value) {
        Objects.requireNonNull(name, "field name must not be null");
        if (value != null) {
            valueMap.put(name, value);
        } else {
            valueMap.remove(name);
        }
        markUpdatedField(name);
    }
}
