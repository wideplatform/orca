package com.iostate.orca.metadata.cascade;


import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.CascadeConfig;
import com.iostate.orca.metadata.HasAndBelongsToMany;
import com.iostate.orca.metadata.inverse.IndirectInverse;
import com.iostate.orca.metadata.inverse.Inverse;

import java.util.Collection;
import java.util.Objects;

public class HasAndBelongsToManyCascade implements Cascade {

    private final HasAndBelongsToMany field;
    private final Collection<PersistentObject> values;
    private final CascadeConfig cascadeConfig;

    public HasAndBelongsToManyCascade(
            HasAndBelongsToMany field,
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
        return new IndirectInverse(field.getMiddleTable(), values, entityManager);
    }
}
