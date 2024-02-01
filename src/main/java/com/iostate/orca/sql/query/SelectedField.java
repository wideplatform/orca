package com.iostate.orca.sql.query;

import com.iostate.orca.metadata.Field;

class SelectedField {

    private final Field field;
    private final String tableAlias;
    private final int index;

    SelectedField(Field field, String tableAlias, int index) {
        this.field = field;
        this.tableAlias = tableAlias;
        this.index = index;
    }

     Field getField() {
        return field;
    }

     String getTableAlias() {
        return tableAlias;
    }

    int getIndex() {
        return index;
    }
}
