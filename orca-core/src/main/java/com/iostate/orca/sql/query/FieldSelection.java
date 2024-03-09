package com.iostate.orca.sql.query;

import java.util.ArrayList;
import java.util.List;

// Represents the selected fields of an entity model
class FieldSelection {

    private SelectedField idField;
    private final List<SelectedField> selectedFields = new ArrayList<>();

    public SelectedField getIdField() {
        return idField;
    }

    public List<SelectedField> getSelectedFields() {
        return selectedFields;
    }

    public void add(SelectedField sf) {
        selectedFields.add(sf);
        if (sf.getField().isId()) {
            idField = sf;
        }
    }
}
