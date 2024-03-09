package com.iostate.orca.sql.query;

class TableAliasGenerator {

    private int count;

    String generate() {
        count++;
        return "t" + count;
    }
}
