package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.inverse.DirectInverse;
import com.iostate.orca.metadata.inverse.Inverse;
import com.iostate.orca.metadata.inverse.VoidInverse;

import java.util.Collections;
import java.util.Objects;

public class HasOneCascade implements Cascade {

    private final AssociationField field;
    private final PersistentObject value;
    private final CascadeConfig cascadeConfig;

    public HasOneCascade(AssociationField field, PersistentObject value, CascadeConfig cascadeConfig) {
        this.field = Objects.requireNonNull(field);
        this.value = value;
        this.cascadeConfig = Objects.requireNonNull(cascadeConfig);
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (value != null && cascadeConfig.isPersist()) {
            // Should use merge?
            entityManager.merge(value);
        }
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (value != null && cascadeConfig.isMerge()) {
            entityManager.merge(value);
        }
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (value != null && cascadeConfig.isRemove()) {
            entityManager.remove(value);
        }
    }

    @Override
    public Inverse getInverse(EntityManager entityManager) {
        if (value != null && field.getMappedByFieldName() != null) {
            Field mappedByField = field.getTargetModelRef().model().findFieldByName(field.getMappedByFieldName());
            return new DirectInverse(mappedByField, Collections.singleton(value));
        } else {
            return new VoidInverse();
        }
    }

    public AssociationField getField() {
        return field;
    }

    public Object getTargetId() {
        return field.getTargetModelRef().model().getIdField().getValue(value);
    }
}
