package com.iostate.orca.sql.query;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.query.predicate.Predicates;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CacheContext {
    private final MapMap<EntityModel, Object, PersistentObject> byIdCache = new MapMap<>();

    void put(EntityModel entityModel, Object id, PersistentObject po) {
        byIdCache.put(entityModel, id, po);
    }

    PersistentObject get(EntityModel entityModel, Object id) {
        return byIdCache.get(entityModel, id);
    }
}

class QueryCacheContext {

    private final Map<Field, FieldLevelCache> fieldsToCaches = new HashMap<>();

    void putById(EntityModel entityModel, Object id, PersistentObject po) {
        FieldLevelCache fieldLevelCache = fieldsToCaches.computeIfAbsent(entityModel.getIdField(), k -> new FieldLevelCache(entityModel, entityModel.getIdField()));
        fieldLevelCache.resolve(id, po);
    }

    void addCallback(EntityModel entityModel, Field field, Object key, FieldQueryCallback callback) {
        FieldLevelCache fieldLevelCache = fieldsToCaches.computeIfAbsent(field, k -> new FieldLevelCache(entityModel, field));
        fieldLevelCache.addCallback(key, callback);
    }

//    void execute(Connection connection) throws SQLException {
//        for (FieldLevelCache fieldLevelCache : fieldsToCaches.values()) {
//            fieldLevelCache.execute(connection, this);
//        }
//    }

    public static class FieldLevelCache {
        private final EntityModel entityModel;
        private final Field field;
        private final Map<Object, KeyLevelCache> keysToCaches = new HashMap<>();

        FieldLevelCache(EntityModel entityModel, Field field) {
            this.entityModel = entityModel;
            this.field = field;
        }

        void resolve(Object key, Object value) {
            Objects.requireNonNull(key);
            List<PersistentObject> results = field.isId()
                    ? Collections.singletonList((PersistentObject) value)
                    : (List<PersistentObject>) value;
            keysToCaches.computeIfAbsent(key, k -> new KeyLevelCache())
                    .resolve(results);
        }

        void addCallback(Object key, FieldQueryCallback callback) {
            Objects.requireNonNull(key);
            keysToCaches.computeIfAbsent(key, k -> new KeyLevelCache())
                    .addCallback(callback);
        }

        void execute(Connection connection, CacheContext cacheContext) throws SQLException {
            Collection<Object> pendingKeys = pendingKeys();
            if (pendingKeys.isEmpty()) {
                return;
            }

            System.out.println("pendingKeys: " + pendingKeys);

            if (field.isId()) {
                List<PersistentObject> results = new QueryTree(entityModel, cacheContext)
                        .addFilter(Predicates.in(field.getName(), pendingKeys))
                        .execute(connection);
                for (PersistentObject result : results) {
                    Object key = field.getValue(result);
                    resolve(key, result);
                }
            } else {
                List<PersistentObject> results = new QueryTree(entityModel, cacheContext)
                        .addFilter(Predicates.in(field.getName(), pendingKeys))
                        .execute(connection);
                Map<Object, List<PersistentObject>> groupedResults = results.stream()
                        .collect(Collectors.groupingBy(po -> po.getForeignKeyValue(field.getColumnName())));
                keysToCaches.forEach((key, cache) -> cache.resolve(groupedResults.get(key)));
            }
        }

        private Collection<Object> pendingKeys() {
            Collection<Object> pendingKeys = new ArrayList<>();
            keysToCaches.forEach((key, cache) -> {
                if (cache.callbacks.size() > 0) {
                    pendingKeys.add(key);
                }
            });
            return pendingKeys;
        }
    }

    public static class KeyLevelCache {
        private List<PersistentObject> results = null;
        private final List<FieldQueryCallback> callbacks = new ArrayList<>();

        public void resolve(List<PersistentObject> results) {
            this.results = results;
            callbacks.forEach(callback -> callback.accept(results));
            callbacks.clear();
        }

        void addCallback(FieldQueryCallback callback) {
            if (results != null) {
                callback.accept(results);
            } else {
                callbacks.add(callback);
            }
        }
    }

    @FunctionalInterface
    public interface FieldQueryCallback {
        void accept(List<PersistentObject> results);
    }
}
