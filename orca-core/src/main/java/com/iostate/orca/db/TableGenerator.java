package com.iostate.orca.db;

import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.EntityModelDiff;

import java.util.List;

/**
 * Generates DDL for a table (assuming to be executed in tenant context)
 */
public interface TableGenerator {

    List<String> create(EntityModel entityModel);

    List<String> drop(EntityModel entityModel);

    List<String> alter(EntityModel entityModel, EntityModelDiff diff);
}
