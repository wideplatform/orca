package com.iostate.example.persistence.entity;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("")
public class Order extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private java.util.List<OrderEntry> entries = new java.util.ArrayList<>();
    private java.time.Instant created_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }

    public java.util.List<OrderEntry> getEntries() {
        return entries;
    }

    public void setEntries(java.util.List<OrderEntry> entries) {
        this.entries = entries;
        markUpdatedField("entries");
    }

    public java.time.Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.time.Instant created_at) {
        this.created_at = created_at;
        markUpdatedField("created_at");
    }

    private static final Map<String, Function<Order, Object>> GETTERS;

    static {
        Map<String, Function<Order, Object>> getters = new HashMap<>();
        getters.put("id", Order::getId);
        getters.put("entries", Order::getEntries);
        getters.put("created_at", Order::getCreated_at);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<Order, Object>> SETTERS;

    static {
        Map<String, BiConsumer<Order, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("entries", (object, value) -> object.setEntries((java.util.List<OrderEntry>) value));
        setters.put("created_at", (object, value) -> object.setCreated_at((java.time.Instant) value));
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
        if (!(o instanceof Order that)) return false;

        if (getId() != null) return getId().equals(that.getId());
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
