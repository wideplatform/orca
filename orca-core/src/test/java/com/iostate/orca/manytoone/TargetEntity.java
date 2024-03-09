package com.iostate.orca.manytoone;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("manytoone")
public class TargetEntity extends com.iostate.orca.api.BasePO {
    private Long id;
    private Integer intValue;

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

    private static final Map<String, Function<TargetEntity, Object>> GETTERS;

    static {
        Map<String, Function<TargetEntity, Object>> getters = new HashMap<>();
        getters.put("id", TargetEntity::getId);
        getters.put("intValue", TargetEntity::getIntValue);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<TargetEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<TargetEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("intValue", (object, value) -> object.setIntValue((Integer) value));
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
