package com.iostate.orca.sql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiMap<K, V> {
    private final Map<K, List<V>> map = new HashMap<>();

    public void put(K key, V value) {
        List<V> list = map.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(value);
    }

    public List<V> get(K key) {
        return map.get(key);
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Set<V> valueSet() {
        Set<V> collected = new HashSet<>();
        for (List<V> list : map.values()) {
            collected.addAll(list);
        }
        return collected;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
