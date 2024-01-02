package com.iostate.orca.sql;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.cascade.Cascade;
import com.iostate.orca.metadata.cascade.PluralAssociationCascade;
import com.iostate.orca.metadata.cascade.SingularAssociationCascade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Record-style PO representation for persisting, also a tree having cascade nodes.
 */
class PersistableRecord {
    private final PersistentObject parent;
    private final EntityManager entityManager;

    private final LinkedHashMap<String, Object> columnValues = new LinkedHashMap<>();
    private final Collection<SingularAssociationCascade> singularCascades = new ArrayList<>();
    private final Collection<PluralAssociationCascade> pluralCascades = new ArrayList<>();

    PersistableRecord(Collection<Field> fields, PersistentObject entity, EntityManager entityManager) {
        this.parent = entity;
        this.entityManager = entityManager;
        for (Field field : fields) {
            add(field, entity);
        }
    }

    private void add(Field field, PersistentObject entity) {
        if (field.isAssociation()) {
            AssociationField assoc = (AssociationField) field;

            if (assoc.isInverse()) {
                Field inverseTargetIdField = assoc.getTargetModelRef().model().getIdField();
                Object inverseTargetEntity = field.getValue(entity);
                if (inverseTargetEntity != null) {
                    Object inverseTargetId = inverseTargetIdField.getValue(inverseTargetEntity);
                    columnValues.put(field.getColumnName(), inverseTargetId);
                }
                // Complete
                return;
            }

            Cascade cascade = assoc.getCascade(entity);
            if (assoc.isSingular()) {
                // This entry will be refreshed after cascading
                // When persisting with generated ID, current value is a null
                // When persisting with given ID or updating, current value is a non-null ID
                columnValues.put(field.getColumnName(), assoc.getTargetModelRef().model().getIdField().getValue(entity));

                singularCascades.add((SingularAssociationCascade) cascade);
            } else {
                pluralCascades.add((PluralAssociationCascade) cascade);
            }
        } else {
            columnValues.put(field.getColumnName(), field.getValue(entity));
        }
    }

    LinkedHashMap<String, Object> getColumnValues() {
        return columnValues;
    }

    void prePersist() {
        singularCascades.forEach(cascade -> {
            // persist target first
            cascade.persist(entityManager);
            // fill target ID into source FK column
            columnValues.put(cascade.getField().getColumnName(), cascade.getTargetId());
        });
    }

    void postPersist() {
        singularCascades.forEach(cascade -> {
            cascade.getInverse(entityManager).fill(parent);
        });
        pluralCascades.forEach(cascade -> {
            cascade.getInverse(entityManager).fill(parent);
            cascade.persist(entityManager);
        });
    }

    void preUpdate() {
    }

    void postUpdate() {
        singularCascades.forEach(cascade -> cascade.merge(entityManager));
        pluralCascades.forEach(cascade -> cascade.merge(entityManager));
    }
}
