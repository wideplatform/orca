package com.iostate.orca.sql.query;

import java.util.HashMap;
import java.util.Map;

public class MapMap<K1, K2, V> {
    private final Map<K1, Map<K2, V>> l1map = new HashMap<>();

    public V put(K1 key1, K2 key2, V value) {
        Map<K2, V> l2map = l1map.computeIfAbsent(key1, k -> new HashMap<>());
        return l2map.put(key2, value);
    }

    public V get(K1 key1, K2 key2) {
        Map<K2, V> l2map = l1map.get(key1);
        if (l2map == null) {
            return null;
        }
        return l2map.get(key2);
    }
}
