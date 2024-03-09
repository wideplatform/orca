package com.iostate.orca.metadata;

import com.iostate.orca.api.PersistentObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PersistentObjectUtils {

    private PersistentObjectUtils() {}

    public static Map<Object, PersistentObject> indexById(EntityModel entityModel, Collection<PersistentObject> pos) {
        Map<Object, PersistentObject> idsToPos = new HashMap<>();
        for (PersistentObject po : pos) {
            Object id = entityModel.getIdValue(po);
            idsToPos.put(id, po);
        }
        return idsToPos;
    }
}
