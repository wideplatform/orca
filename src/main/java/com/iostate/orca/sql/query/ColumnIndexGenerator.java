package com.iostate.orca.sql.query;

import com.iostate.orca.metadata.Field;

import java.util.ArrayList;
import java.util.List;

class ColumnIndexGenerator {

    private final List<SelectedField> selectedFields = new ArrayList<>();

    SelectedField newSelectedField(Field field, String tableAlias) {
        SelectedField sf = new SelectedField(field, tableAlias, selectedFields.size() + 1);
        selectedFields.add(sf);
        return sf;
    }

    public List<SelectedField> getSelectedFields() {
        return selectedFields;
    }
}
