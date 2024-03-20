package com.iostate.orca.metadata;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.core.InternalEntityManager;

public class MiddleTableImage {
    private final boolean isOwning;
    private final MiddleTable middleTable;

    MiddleTableImage(ManyToMany mm) {
        if (mm.getMappedByFieldName() == null) {
            isOwning = true;
            middleTable = mm.getMiddleTable();
        } else {
            isOwning = false;
            middleTable = ((ManyToMany) mm.getMappedByField()).getMiddleTable();
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

    public void put(EntityObject source, EntityObject target, EntityManager entityManager) {
        if (isOwning) {
            middleTable.put(source, target, (InternalEntityManager) entityManager);
        } else {
            middleTable.put(target, source, (InternalEntityManager) entityManager);
        }
    }

    public void remove(EntityObject source, EntityObject target, EntityManager entityManager) {
        if (isOwning) {
            middleTable.remove(source, target, (InternalEntityManager) entityManager);
        } else {
            middleTable.remove(target, source, (InternalEntityManager) entityManager);
        }
    }
}
