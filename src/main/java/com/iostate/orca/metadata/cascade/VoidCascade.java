package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.metadata.inverse.Inverse;
import com.iostate.orca.metadata.inverse.VoidInverse;

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

    @Override
    public Inverse getInverse(EntityManager entityManager) {
        return new VoidInverse();
    }
}
