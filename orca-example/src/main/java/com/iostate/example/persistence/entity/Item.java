package com.iostate.example.persistence.entity;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("")
public class Item extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private String name;
    private Boolean visible;
    private Integer quantity;
    private java.math.BigDecimal price;
    private java.time.Instant created_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        markUpdatedField("name");
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
        markUpdatedField("visible");
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        markUpdatedField("quantity");
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
        markUpdatedField("price");
    }

    public java.time.Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.time.Instant created_at) {
        this.created_at = created_at;
        markUpdatedField("created_at");
    }

    private static final Map<String, Function<Item, Object>> GETTERS;

    static {
        Map<String, Function<Item, Object>> getters = new HashMap<>();
        getters.put("id", Item::getId);
        getters.put("name", Item::getName);
        getters.put("visible", Item::getVisible);
        getters.put("quantity", Item::getQuantity);
        getters.put("price", Item::getPrice);
        getters.put("created_at", Item::getCreated_at);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<Item, Object>> SETTERS;

    static {
        Map<String, BiConsumer<Item, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("name", (object, value) -> object.setName((String) value));
        setters.put("visible", (object, value) -> object.setVisible((Boolean) value));
        setters.put("quantity", (object, value) -> object.setQuantity((Integer) value));
        setters.put("price", (object, value) -> object.setPrice((java.math.BigDecimal) value));
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
        if (!(o instanceof Item that)) return false;

        if (getId() != null) return getId().equals(that.getId());
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
