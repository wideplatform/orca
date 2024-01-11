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

public class SingularAssociationCascade implements Cascade {

    private final AssociationField field;
    private final PersistentObject value;
    private final CascadeConfig cascadeConfig;

    public SingularAssociationCascade(AssociationField field, PersistentObject value, CascadeConfig cascadeConfig) {
        this.field = field;
        this.value = value;
        this.cascadeConfig = cascadeConfig;
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (cascadeConfig.isPersist() && value != null) {
            // Should use merge?
            entityManager.merge(value);
        }
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (cascadeConfig.isMerge() && value != null) {
            entityManager.merge(value);
        }
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (cascadeConfig.isRemove() && value != null) {
            entityManager.remove(value);
        }
    }

    @Override
    public Inverse getInverse(EntityManager entityManager) {
        if (field.getMappedByFieldName() != null) {
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
