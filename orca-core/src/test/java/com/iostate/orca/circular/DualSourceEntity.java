package com.iostate.orca.circular;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("circular")
public class DualSourceEntity extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private String strValue;
    private DualTargetEntity target1;
    private DualTargetEntity target2;

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
    public DualTargetEntity getTarget1() {
        return target1;
    }

    public void setTarget1(DualTargetEntity target1) {
        this.target1 = target1;
        markUpdatedField("target1");
    }
    public DualTargetEntity getTarget2() {
        return target2;
    }

    public void setTarget2(DualTargetEntity target2) {
        this.target2 = target2;
        markUpdatedField("target2");
    }

    private static final Map<String, Function<DualSourceEntity, Object>> GETTERS;

    static {
        Map<String, Function<DualSourceEntity, Object>> getters = new HashMap<>();
        getters.put("id", DualSourceEntity::getId);
        getters.put("strValue", DualSourceEntity::getStrValue);
        getters.put("target1", DualSourceEntity::getTarget1);
        getters.put("target2", DualSourceEntity::getTarget2);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<DualSourceEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<DualSourceEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("strValue", (object, value) -> object.setStrValue((String) value));
        setters.put("target1", (object, value) -> object.setTarget1((DualTargetEntity) value));
        setters.put("target2", (object, value) -> object.setTarget2((DualTargetEntity) value));
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
