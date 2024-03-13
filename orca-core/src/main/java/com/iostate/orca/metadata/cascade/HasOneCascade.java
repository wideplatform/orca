package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.HasOne;

import java.util.Objects;

public class HasOneCascade implements Cascade {

    private final Field mappedByField;
    private final EntityObject source;
    private final EntityObject target;
    private final CascadeConfig cascadeConfig;

    public HasOneCascade(HasOne field, EntityObject source, CascadeConfig cascadeConfig) {
        this.mappedByField = Objects.requireNonNull(field).getMappedByField();
        this.source = Objects.requireNonNull(source);
        this.target = (EntityObject) field.getValue(source);
        this.cascadeConfig = Objects.requireNonNull(cascadeConfig);
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (target != null) {
            if (!mappedByField.isUpdated(target)) {
                mappedByField.setValue(target, source);
            }
            if (cascadeConfig.isPersist()) {
                // Should use merge?
                entityManager.merge(target);
            }
        }
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (target != null) {
            if (!mappedByField.isUpdated(target)) {
                mappedByField.setValue(target, source);
            }
            if (cascadeConfig.isMerge()) {
                entityManager.merge(target);
            }
        }
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (target != null) {
            if (mappedByField.isNullable()) {
                mappedByField.setValue(target, null);
            }
            if (cascadeConfig.isRemove()) {
                entityManager.remove(target);
            }
        }
    }

}
