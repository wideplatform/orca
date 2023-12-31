package com.iostate.orca.metadata.cascade;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.metadata.inverse.Inverse;

public interface Cascade {

    void persist(EntityManager entityManager);

    void merge(EntityManager entityManager);

    void remove(EntityManager entityManager);

    Inverse getInverse(EntityManager entityManager);
}
