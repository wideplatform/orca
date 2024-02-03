package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.HasMany;

import java.util.Collection;
import java.util.Objects;

public class HasManyCascade implements Cascade {

    private final HasMany field;
    private final PersistentObject source;
    private final Collection<PersistentObject> targets;
    private final CascadeConfig cascadeConfig;

    public HasManyCascade(HasMany field,
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
        if (targets != null && cascadeConfig.isPersist()) {
            Field mappedByField = field.getMappedByField();
            for (Object target : targets) {
                mappedByField.setValue(target, source);
                // Should use merge?
                entityManager.merge(target);
            }
        }
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (targets != null && cascadeConfig.isMerge()) {
            for (Object target : targets) {
                entityManager.merge(target);
            }
        }
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (targets != null && cascadeConfig.isRemove()) {
            for (Object target : targets) {
                entityManager.remove(target);
            }
        }
    }
}
