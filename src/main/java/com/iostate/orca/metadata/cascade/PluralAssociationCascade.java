package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.HasMany;
import com.iostate.orca.metadata.inverse.DirectInverse;
import com.iostate.orca.metadata.inverse.IndirectInverse;
import com.iostate.orca.metadata.inverse.Inverse;

import java.util.Collection;
import java.util.Objects;

public class PluralAssociationCascade implements Cascade {

    private final HasMany field;
    private final Collection<PersistentObject> values;
    private final CascadeConfig cascadeConfig;

    public PluralAssociationCascade(
            HasMany field,
            Collection<PersistentObject> values,
            CascadeConfig cascadeConfig) {
        this.field = Objects.requireNonNull(field);
        this.values = Objects.requireNonNull(values);
        this.cascadeConfig = Objects.requireNonNull(cascadeConfig);
    }

    @Override
    public void persist(EntityManager entityManager) {
        if (cascadeConfig.isPersist() && values != null) {
            for (Object value : values) {
                // Should use merge?
                entityManager.merge(value);
            }
        }
    }

    @Override
    public void merge(EntityManager entityManager) {
        if (cascadeConfig.isMerge() && values != null) {
            for (Object value : values) {
                entityManager.merge(value);
            }
        }
    }

    @Override
    public void remove(EntityManager entityManager) {
        if (cascadeConfig.isRemove() && values != null) {
            for (Object value : values) {
                entityManager.remove(value);
            }
        }
    }

    @Override
    public Inverse getInverse(EntityManager entityManager) {
        if (field.getMappedByFieldName() != null) {
            Field mappedByField = field.getTargetModelRef().model().findFieldByName(field.getMappedByFieldName());
            return new DirectInverse(mappedByField, values);
        } else {
            return new IndirectInverse(field.getMiddleTable(), values, entityManager);
        }
    }
}
