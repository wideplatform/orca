package com.iostate.orca.sql;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.BelongsTo;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.cascade.Cascade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Record-style PO representation for persisting, also a tree having cascade nodes.
 */
class PersistableRecord {
    private final PersistentObject entity;
    private final EntityManager entityManager;

    private final LinkedHashMap<String, Object> columnValues = new LinkedHashMap<>();

    private final Collection<Cascade> cascades = new ArrayList<>();

    PersistableRecord(Collection<Field> fields, PersistentObject entity, EntityManager entityManager) {
        this.entity = Objects.requireNonNull(entity);
        this.entityManager = Objects.requireNonNull(entityManager);
        for (Field field : fields) {
            add(field);
        }
    }

    private void add(Field field) {
        if (field.isAssociation()) {
            AssociationField assoc = (AssociationField) field;
            if (assoc instanceof BelongsTo) {
                Object target = assoc.getValue(entity);
                if (target != null) {
                    Field targetIdField = assoc.getTargetModelRef().model().getIdField();
                    Object targetId = targetIdField.getValue(target);
                    // This entry will be refreshed after cascading
                    // When persisting with generated ID, current value is a null
                    // When persisting with given ID or updating, current value is a non-null ID
                    columnValues.put(assoc.getColumnName(), targetId);
                }
            }

            cascades.add(assoc.getCascade(entity));
        } else {
            columnValues.put(field.getColumnName(), field.getValue(entity));
        }
    }

    LinkedHashMap<String, Object> getColumnValues() {
        return columnValues;
    }

    void prePersist() {
    }

    void postPersist() {
        cascades.forEach(cascade -> cascade.persist(entityManager));
    }

    void preUpdate() {
    }

    void postUpdate() {
        cascades.forEach(cascade -> cascade.merge(entityManager));
    }

    public void preDelete() {
        cascades.forEach(cascade -> cascade.remove(entityManager));
    }

    public void postDelete() {
    }
}
