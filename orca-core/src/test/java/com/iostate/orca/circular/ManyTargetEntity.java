package com.iostate.orca.circular;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("circular")
public class ManyTargetEntity extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private Integer intValue;
    private java.util.List<ManySourceEntity> sources = new java.util.ArrayList<>();

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
    public java.util.List<ManySourceEntity> getSources() {
        return sources;
    }

    public void setSources(java.util.List<ManySourceEntity> sources) {
        this.sources = sources;
        markUpdatedField("sources");
    }

    private static final Map<String, Function<ManyTargetEntity, Object>> GETTERS;

    static {
        Map<String, Function<ManyTargetEntity, Object>> getters = new HashMap<>();
        getters.put("id", ManyTargetEntity::getId);
        getters.put("intValue", ManyTargetEntity::getIntValue);
        getters.put("sources", ManyTargetEntity::getSources);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<ManyTargetEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<ManyTargetEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("intValue", (object, value) -> object.setIntValue((Integer) value));
        setters.put("sources", (object, value) -> object.setSources((java.util.List<ManySourceEntity>) value));
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
        markUpdatedField(name);
    }
}
