package com.iostate.orca.circular;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("circular")
public class ManySourceEntity extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private String strValue;
    private java.util.List<ManyTargetEntity> targets = new java.util.ArrayList<>();

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

    public java.util.List<ManyTargetEntity> getTargets() {
        return targets;
    }

    public void setTargets(java.util.List<ManyTargetEntity> targets) {
        this.targets = targets;
        markUpdatedField("targets");
    }

    private static final Map<String, Function<ManySourceEntity, Object>> GETTERS;

    static {
        Map<String, Function<ManySourceEntity, Object>> getters = new HashMap<>();
        getters.put("id", ManySourceEntity::getId);
        getters.put("strValue", ManySourceEntity::getStrValue);
        getters.put("targets", ManySourceEntity::getTargets);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<ManySourceEntity, Object>> SETTERS;

    static {
        Map<String, BiConsumer<ManySourceEntity, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("strValue", (object, value) -> object.setStrValue((String) value));
        setters.put("targets", (object, value) -> object.setTargets((java.util.List<ManyTargetEntity>) value));
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManySourceEntity that)) return false;

        if (getId() != null) return getId().equals(that.getId());
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
