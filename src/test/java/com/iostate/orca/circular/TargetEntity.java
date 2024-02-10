package com.iostate.orca.circular;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("circular")
public class TargetEntity extends com.iostate.orca.api.BasePO {
    private Long id;
    private Integer integer;
    private java.util.List<SourceEntity> sources = new java.util.ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }
    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
        markUpdatedField("integer");
    }
    public java.util.List<SourceEntity> getSources() {
        return sources;
    }

    public void setSources(java.util.List<SourceEntity> sources) {
        this.sources = sources;
        markUpdatedField("sources");
    }

    private static final Map<String, Function<TargetEntity, Object>> GETTERS;

    static {
        Map<String, Function<TargetEntity, Object>> getters = new HashMap<>();
        getters.put("id", TargetEntity::getId);
        getters.put("integer", TargetEntity::getInteger);
        getters.put("sources", TargetEntity::getSources);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<TargetEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<TargetEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("integer", (object, value) -> object.setInteger((Integer) value));
        setters.put("sources", (object, value) -> object.setSources((java.util.List<SourceEntity>) value));
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
