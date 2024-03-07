package com.iostate.orca.circular;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("circular")
public class DualTargetEntity extends com.iostate.orca.api.BasePO {
    private Long id;
    private String strValue;
    private DualSourceEntity source1;
    private DualSourceEntity source2;

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
    public DualSourceEntity getSource1() {
        return source1;
    }

    public void setSource1(DualSourceEntity source1) {
        this.source1 = source1;
        markUpdatedField("source1");
    }
    public DualSourceEntity getSource2() {
        return source2;
    }

    public void setSource2(DualSourceEntity source2) {
        this.source2 = source2;
        markUpdatedField("source2");
    }

    private static final Map<String, Function<DualTargetEntity, Object>> GETTERS;

    static {
        Map<String, Function<DualTargetEntity, Object>> getters = new HashMap<>();
        getters.put("id", DualTargetEntity::getId);
        getters.put("strValue", DualTargetEntity::getStrValue);
        getters.put("source1", DualTargetEntity::getSource1);
        getters.put("source2", DualTargetEntity::getSource2);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<DualTargetEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<DualTargetEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("strValue", (object, value) -> object.setStrValue((String) value));
        setters.put("source1", (object, value) -> object.setSource1((DualSourceEntity) value));
        setters.put("source2", (object, value) -> object.setSource2((DualSourceEntity) value));
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
