package com.iostate.orca.core;

import com.iostate.orca.api.BaseEntityObject;
import com.iostate.orca.api.EntityManagerFactory;
import com.iostate.orca.metadata.Association;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation for no-code development
 */
public final class CommonEntityObject extends BaseEntityObject {
    private final EntityModel model;
    private final Map<String, Object> valueMap = new ConcurrentHashMap<>();
    private final Set<String> loadedLazyFields = ConcurrentHashMap.newKeySet();

    public CommonEntityObject(EntityModel model) {
        this.model = model;
    }

    public String getModelName() {
        return model.getName();
    }

    @Override
    public Object getFieldValue(String name) {
        Objects.requireNonNull(name, "field name must not be null");
        if (model.findFieldByName(name) instanceof Association a
                && a.getFetchType() == FetchType.LAZY
                && loadedLazyFields.add(name)) {
            EntityManagerFactory.getDefault().loadLazyField(this, name);
        }
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

    @Override
    public String toString() {
        return model.getName() + "{" + valueMap.get(model.getIdField().getName()) + "}";
    }
}
