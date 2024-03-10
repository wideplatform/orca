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

    PersistentObject find(String modelName, Object id);

    <T> List<T> findBy(Class<T> entityClass, String objectPath, Object fieldValue);

    List<PersistentObject> findBy(String modelName, String objectPath, Object fieldValue);
}
