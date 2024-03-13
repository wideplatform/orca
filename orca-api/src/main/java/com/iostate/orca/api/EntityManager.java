package com.iostate.orca.api;

import java.util.List;

public interface EntityManager {

    void persist(EntityObject entity);

    void update(EntityObject entity);

    void merge(EntityObject entity);

    void remove(Class<? extends EntityObject> entityClass, Object id);

    void remove(EntityObject entity);

    void refresh(EntityObject entity);

    <T extends EntityObject> T find(Class<T> entityClass, Object id);

    EntityObject find(String modelName, Object id);

    <T extends EntityObject> List<T> findBy(Class<T> entityClass, String objectPath, Object fieldValue);

    List<EntityObject> findBy(String modelName, String objectPath, Object fieldValue);
}
