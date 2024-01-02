package com.iostate.orca.metadata.inverse;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.MiddleTable;

import java.util.Collection;

/**
 * Based on middle relation table
 */
public class IndirectInverse implements Inverse {

    private final MiddleTable middleTable;
    private final Collection<PersistentObject> targets;
    private final EntityManager entityManager;

    public IndirectInverse(MiddleTable middleTable, Collection<PersistentObject> targets, EntityManager entityManager) {
        this.middleTable = middleTable;
        this.targets = targets;
        this.entityManager = entityManager;
    }

    @Override
    public void fill(PersistentObject entity) {
        for (PersistentObject target : targets) {
            middleTable.put(entity, target, entityManager);
        }
    }
}
