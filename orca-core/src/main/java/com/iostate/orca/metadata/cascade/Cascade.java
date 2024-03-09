package com.iostate.orca.metadata.cascade;

import com.iostate.orca.api.EntityManager;

public interface Cascade {

    void persist(EntityManager entityManager);

    void merge(EntityManager entityManager);

    void remove(EntityManager entityManager);
}
