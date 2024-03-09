package com.iostate.orca.core;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.sql.SqlHelper;

/**
 * A specialized interface for internal usage
 */
public interface InternalEntityManager extends EntityManager {

    PersistentObject find(EntityModel entityModel, Object id);

    SqlHelper getSqlHelper();
}
