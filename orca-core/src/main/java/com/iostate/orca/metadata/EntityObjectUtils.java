package com.iostate.orca.metadata;

import com.iostate.orca.api.EntityObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EntityObjectUtils {

    private EntityObjectUtils() {}

    public static Map<Object, EntityObject> indexById(EntityModel entityModel, Collection<EntityObject> entities) {
        Map<Object, EntityObject> idsToPos = new HashMap<>();
        for (EntityObject entity : entities) {
            Object id = entityModel.getIdValue(entity);
            idsToPos.put(id, entity);
        }
        return idsToPos;
    }
}
