package com.iostate.orca.circular;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("circular")
public class ChildEntity extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private Integer intValue;
    private ParentEntity parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }
    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
        markUpdatedField("intValue");
    }
    public ParentEntity getParent() {
        return parent;
    }

    public void setParent(ParentEntity parent) {
        this.parent = parent;
        markUpdatedField("parent");
    }

    private static final Map<String, Function<ChildEntity, Object>> GETTERS;

    static {
        Map<String, Function<ChildEntity, Object>> getters = new HashMap<>();
        getters.put("id", ChildEntity::getId);
        getters.put("intValue", ChildEntity::getIntValue);
        getters.put("parent", ChildEntity::getParent);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<ChildEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<ChildEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("intValue", (object, value) -> object.setIntValue((Integer) value));
        setters.put("parent", (object, value) -> object.setParent((ParentEntity) value));
        SETTERS = Collections.unmodifiableMap(setters);
    }

    @Override
    public Object getFieldValue(String name) {
        Objects.requireNonNull(name, "field name must not be null");
        return GETTERS.get(name)
            .apply(this);
    }

    @Override
    public void setFieldValue(String name, Object value) {
        Objects.requireNonNull(name, "field name must not be null");
        SETTERS.get(name)
            .accept(this, value);
    }
}
