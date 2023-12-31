package com.iostate.orca;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("")
public class SimpleEntity extends com.iostate.orca.api.BasePO {
    private Long id;
    private Boolean bool;
    private Integer integer;
    private String string;
    private java.time.Instant dt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }
    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
        markUpdatedField("bool");
    }
    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
        markUpdatedField("integer");
    }
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
        markUpdatedField("string");
    }
    public java.time.Instant getDt() {
        return dt;
    }

    public void setDt(java.time.Instant dt) {
        this.dt = dt;
        markUpdatedField("dt");
    }

    private static final Map<String, Function<SimpleEntity, Object>> GETTERS;

    static {
        Map<String, Function<SimpleEntity, Object>> getters = new HashMap<>();
        getters.put("id", SimpleEntity::getId);
        getters.put("bool", SimpleEntity::getBool);
        getters.put("integer", SimpleEntity::getInteger);
        getters.put("string", SimpleEntity::getString);
        getters.put("dt", SimpleEntity::getDt);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<SimpleEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<SimpleEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("bool", (object, value) -> object.setBool((Boolean) value));
        setters.put("integer", (object, value) -> object.setInteger((Integer) value));
        setters.put("string", (object, value) -> object.setString((String) value));
        setters.put("dt", (object, value) -> object.setDt((java.time.Instant) value));
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
