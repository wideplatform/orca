package com.iostate.orca.sql.query;

import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.EntityModel;

// Thread-unsafe cache, usually used as L0 or L1 cache
public class CacheContext {
    private final MapMap<EntityModel, Object, EntityObject> byIdCache = new MapMap<>();

    public void put(EntityModel entityModel, Object id, EntityObject entity) {
        byIdCache.put(entityModel, id, entity);
    }

    public EntityObject get(EntityModel entityModel, Object id) {
        return byIdCache.get(entityModel, id);
    }

    void merge(CacheContext other) {
        byIdCache.merge(other.byIdCache);
    }
}
