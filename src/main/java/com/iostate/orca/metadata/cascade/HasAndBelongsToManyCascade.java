package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.metadata.HasAndBelongsToMany;

import java.util.Collection;
import java.util.Objects;

public class HasAndBelongsToManyCascade implements Cascade {

    private final HasAndBelongsToMany field;
    private final PersistentObject source;
    private final Collection<PersistentObject> targets;
    private final CascadeConfig cascadeConfig;

    public HasAndBelongsToManyCascade(
            HasAndBelongsToMany field,
            PersistentObject source,
            CascadeConfig cascadeConfig) {
        this.field = Objects.requireNonNull(field);
        this.source = Objects.requireNonNull(source);
        //noinspection unchecked
        this.targets = (Collection<PersistentObject>) field.getValue(source);
        this.cascadeConfig = Objects.requireNonNull(cascadeConfig);
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (targets == null) {
            return;
        }
        if (cascadeConfig.isPersist()) {
            for (PersistentObject target : targets) {
                // Should use merge?
                entityManager.merge(target);
            }
        }
        middleTablePut(entityManager);
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (targets == null) {
            return;
        }
        if (cascadeConfig.isMerge()) {
            for (PersistentObject target : targets) {
                entityManager.merge(target);
            }
        }
        middleTablePut(entityManager);
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (targets == null) {
            return;
        }
        middleTableRemove(entityManager);
        if (cascadeConfig.isRemove()) {
            for (PersistentObject target : targets) {
                entityManager.remove(target);
            }
        }
    }

    private void middleTablePut(EntityManager entityManager) {
        for (PersistentObject target : targets) {
            if (target.isPersisted()) {
                field.getMiddleTable().put(source, target, entityManager);
            } else {
                throw new PersistenceException(
                        String.format("Failed to relate non-persisted %s to %s without cascading",
                                field.getTargetModelRef().getName(), field.getSourceModel().getName()));
            }
        }
    }

    private void middleTableRemove(EntityManager entityManager) {
        for (PersistentObject target : targets) {
            if (target.isPersisted()) {
                field.getMiddleTable().remove(source, target, entityManager);
            }
        }
    }
}
