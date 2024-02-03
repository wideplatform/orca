package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.CascadeConfig;

import java.util.Objects;

public class HasOneCascade implements Cascade {

    private final AssociationField field;
    private final PersistentObject source;
    private final PersistentObject target;
    private final CascadeConfig cascadeConfig;

    public HasOneCascade(AssociationField field, PersistentObject source, CascadeConfig cascadeConfig) {
        this.field = Objects.requireNonNull(field);
        this.source = Objects.requireNonNull(source);
        this.target = (PersistentObject) field.getValue(source);
        this.cascadeConfig = Objects.requireNonNull(cascadeConfig);
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (target != null && cascadeConfig.isPersist()) {
            field.getMappedByField().setValue(target, source);
            // Should use merge?
            entityManager.merge(target);
        }
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (target != null && cascadeConfig.isMerge()) {
            field.getMappedByField().setValue(target, source);
            entityManager.merge(target);
        }
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (target != null && cascadeConfig.isRemove()) {
            entityManager.remove(target);
        }
    }
}
