package com.iostate.orca.api;

import java.util.List;

public interface EntityManager {

    void persist(Object entity);

    void update(Object entity);

    void merge(Object entity);

    void remove(Class<?> entityClass, Object id);

    void remove(Object entity);

    void refresh(Object entity);

    <T> T find(Class<T> entityClass, Object id);

    <T> List<T> findByField(Class<T> entityClass, String fieldName, Object fieldValue);

    <T> T fetch(Class<T> entityClass, Object id);

    <T> T ref(Class<T> entityClass, Object id);
}
