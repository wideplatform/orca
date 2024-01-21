package com.iostate.orca.sql.query;

public class ColumnIndexGenerator {

    private int index;

    int generate() {
        index++;
        return index;
    }
}
