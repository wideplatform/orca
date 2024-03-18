package com.iostate.orca.manytoone;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("manytoone")
public class SourceEntity extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private String strValue;
    private TargetEntity target;

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
    public TargetEntity getTarget() {
        return target;
    }

    public void setTarget(TargetEntity target) {
        this.target = target;
        markUpdatedField("target");
    }

    private static final Map<String, Function<SourceEntity, Object>> GETTERS;

    static {
        Map<String, Function<SourceEntity, Object>> getters = new HashMap<>();
        getters.put("id", SourceEntity::getId);
        getters.put("strValue", SourceEntity::getStrValue);
        getters.put("target", SourceEntity::getTarget);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<SourceEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<SourceEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("strValue", (object, value) -> object.setStrValue((String) value));
        setters.put("target", (object, value) -> object.setTarget((TargetEntity) value));
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
