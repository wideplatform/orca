package com.iostate.orca.metadata;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;

public class MiddleTableImage {
    private final boolean isOwning;
    private final MiddleTable middleTable;

    MiddleTableImage(HasAndBelongsToMany mm) {
        if (mm.getMappedByFieldName() == null) {
            isOwning = true;
            middleTable = mm.getMiddleTable();
        } else {
            isOwning = false;
            middleTable = ((HasAndBelongsToMany) mm.getMappedByField()).getMiddleTable();
        }
    }

    public String getTableName() {
        return middleTable.getTableName();
    }

    public String getSourceIdColumn() {
        if (isOwning) {
            return middleTable.getSourceIdColumnName();
        } else {
            return middleTable.getTargetIdColumnName();
        }
    }

    public String getTargetIdColumn() {
        if (isOwning) {
            return middleTable.getTargetIdColumnName();
        } else {
            return middleTable.getSourceIdColumnName();
        }
    }

    public void put(PersistentObject source, PersistentObject target, EntityManager entityManager) {
        if (isOwning) {
            middleTable.put(source, target, entityManager);
        } else {
            middleTable.put(target, source, entityManager);
        }
    }

    public void remove(PersistentObject source, PersistentObject target, EntityManager entityManager) {
        if (isOwning) {
            middleTable.remove(source, target, entityManager);
        } else {
            middleTable.remove(target, source, entityManager);
        }
    }
}
