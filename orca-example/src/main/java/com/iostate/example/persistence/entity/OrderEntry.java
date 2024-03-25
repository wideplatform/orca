package com.iostate.example.persistence.entity;

import java.util.Collections;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@com.iostate.orca.api.Namespace("")
public class OrderEntry extends com.iostate.orca.api.BaseEntityObject {
    private Long id;
    private Integer quantity;
    private Order order;
    private Item item;
    private boolean _itemLoaded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        markUpdatedField("id");
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        markUpdatedField("quantity");
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        markUpdatedField("order");
    }

    public Item getItem() {
        if (!_itemLoaded) {
            com.iostate.orca.api.EntityManagerFactory.getDefault().loadLazyField(this, "item");
        }
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        _itemLoaded = true;
        markUpdatedField("item");
    }

    private static final Map<String, Function<OrderEntry, Object>> GETTERS;

    static {
        Map<String, Function<OrderEntry, Object>> getters = new HashMap<>();
        getters.put("id", OrderEntry::getId);
        getters.put("quantity", OrderEntry::getQuantity);
        getters.put("order", OrderEntry::getOrder);
        getters.put("item", OrderEntry::getItem);
        GETTERS = Collections.unmodifiableMap(getters);
    }

    private static final Map<String, BiConsumer<OrderEntry, Object>> SETTERS;

    static {
        Map<String, BiConsumer<OrderEntry, Object>> setters = new HashMap<>();
        setters.put("id", (object, value) -> object.setId((Long) value));
        setters.put("quantity", (object, value) -> object.setQuantity((Integer) value));
        setters.put("order", (object, value) -> object.setOrder((Order) value));
        setters.put("item", (object, value) -> object.setItem((Item) value));
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
        if (!(o instanceof OrderEntry that)) return false;

        if (getId() != null) return getId().equals(that.getId());
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
