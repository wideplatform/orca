package com.iostate.orca.sql.query;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.EntityModel;

// Thread-unsafe cache, usually used as L0 or L1 cache
public class CacheContext {
    private final MapMap<EntityModel, Object, PersistentObject> byIdCache = new MapMap<>();

    public void put(EntityModel entityModel, Object id, PersistentObject po) {
        byIdCache.put(entityModel, id, po);
    }

    public PersistentObject get(EntityModel entityModel, Object id) {
        return byIdCache.get(entityModel, id);
    }

    void merge(CacheContext other) {
        byIdCache.merge(other.byIdCache);
    }
}
