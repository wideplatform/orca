package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;

/**
 * Do nothing
 */
public class VoidCascade implements Cascade {

    @Override
    public void persist(EntityManager entityManager) {
    }

    @Override
    public void merge(EntityManager entityManager) {
    }

    @Override
    public void remove(EntityManager entityManager) {
    }
}
