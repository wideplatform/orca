package com.iostate.orca.db;

import com.iostate.orca.metadata.EntityModel;

import java.util.Map;

/**
 * Generates DDL for a table (assuming to be executed in tenant context)
 */
public interface TableGenerator {

    /**
     * Generates DDL for the entity table (and its relation tables for associations like ManyToMany)
     *
     * @param model entity model
     * @return tables Vs DDLs
     */
    Map<String, String> create(EntityModel model);
}
