package com.iostate.orca.metadata.inverse;

import com.iostate.orca.metadata.Field;
import com.iostate.orca.api.PersistentObject;

import java.util.Collection;
import java.util.Objects;

/**
 * Based on inverse field on the target
 */
public class DirectInverse implements Inverse {
    private final Field targetInverseField;
    private final Collection<PersistentObject> targets;

    public DirectInverse(Field targetInverseField, Collection<PersistentObject> targets) {
        Objects.requireNonNull(targetInverseField);
        Objects.requireNonNull(targets);
        this.targetInverseField = targetInverseField;
        this.targets = targets;
    }

    @Override
    public void fill(PersistentObject entity) {
        for (Object target : targets) {
            targetInverseField.setValue(target, entity);
        }
    }
}
