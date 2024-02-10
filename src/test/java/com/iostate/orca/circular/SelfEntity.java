package com.iostate.orca.circular;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("circular")
public class SelfEntity extends com.iostate.orca.api.BasePO {
    private Long id;
    private String strValue;
    private SelfEntity target;
    private SelfEntity source;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }
    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
        markUpdatedField("strValue");
    }
    public SelfEntity getTarget() {
        return target;
    }

    public void setTarget(SelfEntity target) {
        this.target = target;
        markUpdatedField("target");
    }
    public SelfEntity getSource() {
        return source;
    }

    public void setSource(SelfEntity source) {
        this.source = source;
        markUpdatedField("source");
    }

    private static final Map<String, Function<SelfEntity, Object>> GETTERS;

    static {
        Map<String, Function<SelfEntity, Object>> getters = new HashMap<>();
        getters.put("id", SelfEntity::getId);
        getters.put("strValue", SelfEntity::getStrValue);
        getters.put("target", SelfEntity::getTarget);
        getters.put("source", SelfEntity::getSource);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<SelfEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<SelfEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("strValue", (object, value) -> object.setStrValue((String) value));
        setters.put("target", (object, value) -> object.setTarget((SelfEntity) value));
        setters.put("source", (object, value) -> object.setSource((SelfEntity) value));
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
