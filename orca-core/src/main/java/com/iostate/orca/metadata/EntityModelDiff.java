package com.iostate.orca.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityModelDiff {

    private String oldTableName;
    private String newTableName;
    private List<Field> addedFields = new ArrayList<>();
    private List<Field> removedFields = new ArrayList<>();

    private EntityModelDiff() {
    }

    public static EntityModelDiff compute(EntityModel old, EntityModel neo) {
        EntityModelDiff diff = new EntityModelDiff();
        diff.oldTableName = old.getTableName();
        if (!Objects.equals(old.getTableName(), neo.getTableName())) {
            diff.newTableName = neo.getTableName();
        }

        for (Field neoField : neo.allFields()) {
            Field oldField = old.findFieldByName(neoField.getName());
            if (oldField == null) {
                diff.addedFields.add(neoField);
            }
        }

        for (Field oldField : old.allFields()) {
            Field neoField = neo.findFieldByName(oldField.getName());
            if (neoField == null) {
                diff.removedFields.add(oldField);
            }
        }

        if (diff.newTableName == null && diff.addedFields.isEmpty() && diff.removedFields.isEmpty()) {
            return null;
        }

        return diff;
    }

    public String getOldTableName() {
        return oldTableName;
    }

    public String getNewTableName() {
        return newTableName;
    }

    public List<Field> getAddedFields() {
        return addedFields;
    }

    public List<Field> getRemovedFields() {
        return removedFields;
    }
}
