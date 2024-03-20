package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.metadata.ManyToMany;

import java.util.Collection;
import java.util.Objects;

public class ManyToManyCascade implements Cascade {

    private final ManyToMany field;
    private final EntityObject source;
    private final Collection<EntityObject> targets;
    private final CascadeConfig cascadeConfig;

    public ManyToManyCascade(
            ManyToMany field,
            EntityObject source,
            CascadeConfig cascadeConfig) {
        this.field = Objects.requireNonNull(field);
        this.source = Objects.requireNonNull(source);
        //noinspection unchecked
        this.targets = (Collection<EntityObject>) field.getValue(source);
        this.cascadeConfig = Objects.requireNonNull(cascadeConfig);
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (targets == null) {
            return;
        }
        if (cascadeConfig.isPersist()) {
            for (EntityObject target : targets) {
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
            for (EntityObject target : targets) {
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
            for (EntityObject target : targets) {
                entityManager.remove(target);
            }
        }
    }

    private void middleTablePut(EntityManager entityManager) {
        for (EntityObject target : targets) {
            if (target.persisted()) {
                field.middleTableImage().put(source, target, entityManager);
            } else {
                throw new PersistenceException(
                        String.format("Failed to relate non-persisted %s to %s without cascading",
                                field.getTargetModelRef().getName(), field.getSourceModel().getName()));
            }
        }
    }

    private void middleTableRemove(EntityManager entityManager) {
        for (EntityObject target : targets) {
            if (target.persisted()) {
                field.middleTableImage().remove(source, target, entityManager);
            }
        }
    }
}
