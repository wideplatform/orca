package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.HasMany;

import java.util.Collection;
import java.util.Objects;

public class HasManyCascade implements Cascade {

    private final Field mappedByField;
    private final PersistentObject source;
    private final Collection<PersistentObject> targets;
    private final CascadeConfig cascadeConfig;

    public HasManyCascade(HasMany field,
                          PersistentObject source,
                          CascadeConfig cascadeConfig) {
        this.mappedByField = Objects.requireNonNull(field).getMappedByField();
        this.source = Objects.requireNonNull(source);
        //noinspection unchecked
        this.targets = (Collection<PersistentObject>) field.getValue(source);
        this.cascadeConfig = Objects.requireNonNull(cascadeConfig);
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (targets != null) {
            for (PersistentObject target : targets) {
                if (!mappedByField.isUpdated(target)) {
                    mappedByField.setValue(target, source);
                }
                if (cascadeConfig.isPersist()) {
                    // Should use merge?
                    entityManager.merge(target);
                }
            }
        }
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (targets != null) {
            for (PersistentObject target : targets) {
                if (!mappedByField.isUpdated(target)) {
                    mappedByField.setValue(target, source);
                }
                if (cascadeConfig.isMerge()) {
                    entityManager.merge(target);
                }
            }
        }
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (targets != null) {
            for (PersistentObject target : targets) {
                if (mappedByField.isNullable()) {
                    mappedByField.setValue(target, null);
                }
                if (cascadeConfig.isRemove()) {
                    entityManager.remove(target);
                }
            }
        }
    }
}
