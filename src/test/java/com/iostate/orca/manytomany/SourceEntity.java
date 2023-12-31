package com.iostate.orca.manytomany;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("manytomany")
public class SourceEntity extends com.iostate.orca.api.BasePO {
    private Long id;
    private String string;
    private java.util.List<TargetEntity> targets = new java.util.ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
        markUpdatedField("string");
    }
    public java.util.List<TargetEntity> getTargets() {
        return targets;
    }

    public void setTargets(java.util.List<TargetEntity> targets) {
        this.targets = targets;
        markUpdatedField("targets");
    }

    private static final Map<String, Function<SourceEntity, Object>> GETTERS;

    static {
        Map<String, Function<SourceEntity, Object>> getters = new HashMap<>();
        getters.put("id", SourceEntity::getId);
        getters.put("string", SourceEntity::getString);
        getters.put("targets", SourceEntity::getTargets);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<SourceEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<SourceEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("string", (object, value) -> object.setString((String) value));
        setters.put("targets", (object, value) -> object.setTargets((java.util.List<TargetEntity>) value));
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
