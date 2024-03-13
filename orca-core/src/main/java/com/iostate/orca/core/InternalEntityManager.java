package com.iostate.orca.core;

import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.sql.SqlHelper;

/**
 * A specialized interface for internal usage
 */
public interface InternalEntityManager extends EntityManager {

    EntityObject find(EntityModel entityModel, Object id);

    SqlHelper getSqlHelper();
}
