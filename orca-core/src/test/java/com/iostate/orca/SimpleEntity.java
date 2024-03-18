package com.iostate.orca;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("")
public class SimpleEntity extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private Boolean boolValue;
    private Integer intValue;
    private Long longValue;
    private java.math.BigDecimal decValue;
    private String strValue;
    private java.time.Instant dtValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }
    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
        markUpdatedField("boolValue");
    }
    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
        markUpdatedField("intValue");
    }
    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
        markUpdatedField("longValue");
    }
    public java.math.BigDecimal getDecValue() {
        return decValue;
    }

    public void setDecValue(java.math.BigDecimal decValue) {
        this.decValue = decValue;
        markUpdatedField("decValue");
    }
    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
        markUpdatedField("strValue");
    }
    public java.time.Instant getDtValue() {
        return dtValue;
    }

    public void setDtValue(java.time.Instant dtValue) {
        this.dtValue = dtValue;
        markUpdatedField("dtValue");
    }

    private static final Map<String, Function<SimpleEntity, Object>> GETTERS;

    static {
        Map<String, Function<SimpleEntity, Object>> getters = new HashMap<>();
        getters.put("id", SimpleEntity::getId);
        getters.put("boolValue", SimpleEntity::getBoolValue);
        getters.put("intValue", SimpleEntity::getIntValue);
        getters.put("longValue", SimpleEntity::getLongValue);
        getters.put("decValue", SimpleEntity::getDecValue);
        getters.put("strValue", SimpleEntity::getStrValue);
        getters.put("dtValue", SimpleEntity::getDtValue);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<SimpleEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<SimpleEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("boolValue", (object, value) -> object.setBoolValue((Boolean) value));
        setters.put("intValue", (object, value) -> object.setIntValue((Integer) value));
        setters.put("longValue", (object, value) -> object.setLongValue((Long) value));
        setters.put("decValue", (object, value) -> object.setDecValue((java.math.BigDecimal) value));
        setters.put("strValue", (object, value) -> object.setStrValue((String) value));
        setters.put("dtValue", (object, value) -> object.setDtValue((java.time.Instant) value));
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
