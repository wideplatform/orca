package com.iostate.orca.core;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.api.CommonEntityObject;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.exception.EntityNotFoundException;
import com.iostate.orca.api.exception.NonUniqueResultException;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.BelongsTo;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.ManyToMany;
import com.iostate.orca.metadata.HasMany;
import com.iostate.orca.metadata.HasOne;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.sql.SqlHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EntityManagerImpl implements InternalEntityManager {

    private final MetadataManager metadataManager;

    private final SqlHelper sqlHelper;

    public EntityManagerImpl(MetadataManager metadataManager, ConnectionProvider connectionProvider) {
        this.metadataManager = metadataManager;
        this.sqlHelper = new SqlHelper(connectionProvider, this);
    }

    @Override
    public void persist(EntityObject entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        if (isPersisted(entity)) {
            throw new IllegalStateException("entity is already persisted thus unable to persist");
        }

        EntityModel entityModel = getEntityModel(entity);
        sqlHelper.insert(entityModel, entity);

//    refresh(entity);
    }

    @Override
    public void update(EntityObject entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        if (!isPersisted(entity)) {
            throw new IllegalStateException("entity is not persisted thus unable to update");
        }

        EntityModel entityModel = getEntityModel(entity);
        sqlHelper.update(entityModel, entity);

//    refresh(entity);
    }

    @Override
    public void merge(EntityObject entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        if (isPersisted(entity)) {
            update(entity);
        } else {
            persist(entity);
        }
    }

    @Override
    public void remove(Class<? extends EntityObject> entityClass, Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        EntityModel entityModel = metadataManager.findEntityByClass(entityClass);
        sqlHelper.deleteById(entityModel, id);
    }

    @Override
    public void remove(EntityObject entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }
        EntityModel entityModel = getEntityModel(entity);
        sqlHelper.deleteEntity(entityModel, entity);
    }

    @Override
    public void refresh(EntityObject entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        EntityModel entityModel = getEntityModel(entity);
        Object id = entityModel.getIdField().getValue(entity);
        if (id == null) {
            throw new EntityNotFoundException("entityName=" + entityModel.getName() + ", id is null");
        }

        Object from = find(entity.getClass(), id);
        if (from == null) {
            throw new EntityNotFoundException("entityName=" + entityModel.getName() + ", id=" + id);
        }

//    try {
//      BeanUtils.copyProperties(entity, from);
//    } catch (IllegalAccessException | InvocationTargetException e) {
//      throw new PersistenceException("Failed to copyProperties", e);
//    }
    }

    @Override
    public <T extends EntityObject> T find(Class<T> entityClass, Object id) {
        EntityModel entityModel = metadataManager.findEntityByClass(entityClass);
        //noinspection unchecked
        return (T) find(entityModel, id);
    }

    @Override
    public EntityObject find(String modelName, Object id) {
        EntityModel entityModel = metadataManager.findEntityByName(modelName);
        return find(entityModel, id);
    }

    @Override
    public EntityObject find(EntityModel entityModel, Object id) {
        Objects.requireNonNull(entityModel);
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        EntityObject entity = sqlHelper.findById(entityModel, id);
        loadAllLazy(entityModel, Collections.singletonList(entity));
        return entity;
    }

    @Override
    public <T extends EntityObject> List<T> findAll(Class<T> entityClass) {
        EntityModel entityModel = metadataManager.findEntityByClass(entityClass);
        //noinspection unchecked
        return (List<T>) sqlHelper.findAll(entityModel);
    }

    @Override
    public List<EntityObject> findAll(String modelName) {
        EntityModel entityModel = metadataManager.findEntityByName(modelName);
        return sqlHelper.findAll(entityModel);
    }

    @Override
    public <T extends EntityObject> List<T> findBy(Class<T> entityClass, String objectPath, Object fieldValue) {
        EntityModel entityModel = metadataManager.findEntityByClass(entityClass);
        //noinspection unchecked
        return (List<T>) findBy(entityModel, objectPath, fieldValue);
    }

    @Override
    public List<EntityObject> findBy(String modelName, String objectPath, Object fieldValue) {
        EntityModel entityModel = metadataManager.findEntityByName(modelName);
        return findBy(entityModel, objectPath, fieldValue);
    }

    private List<EntityObject> findBy(EntityModel entityModel, String objectPath, Object fieldValue) {
        List<EntityObject> entities = sqlHelper.findBy(entityModel, objectPath, fieldValue);
        loadAllLazy(entityModel, entities);
        return entities;
    }

    // TODO implement real lazy loading in generated code
    private void loadAllLazy(EntityModel entityModel, List<EntityObject> entities) {
        Field idField = entityModel.getIdField();
        entityModel.allFields().stream()
                .filter(Field::isAssociation)
                .map(field -> (AssociationField) field)
                .filter(a -> a.getFetchType() == FetchType.LAZY)
                .forEach(a -> {
                    EntityModel targetModel = a.getTargetModelRef().model();
                    Field mappedByField = null;
                    if (a.getMappedByFieldName() != null) {
                        mappedByField = targetModel.findFieldByName(a.getMappedByFieldName());
                    }

                    for (EntityObject entity : entities) {
                        if (a instanceof BelongsTo) {
                            Object fkValue = entity.getForeignKeyValue(a.getColumnName());
                            if (fkValue != null) {
                                Object target = find(targetModel, fkValue);
                                a.setValue(entity, target);
                            }
                        } else if (a instanceof HasOne) {
                            Object id = idField.getValue(entity);
                            List<EntityObject> targets = sqlHelper.findBy(targetModel, mappedByField.getName(), id);
                            if (targets.size() == 1) {
                                a.setValue(entity, targets.get(0));
                            } else if (targets.size() > 1) {
                                throw new NonUniqueResultException(entityModel.getName(), id);
                            }
                            // else: remain null if targets is empty
                        } else if (a instanceof HasMany) {
                            Object id = idField.getValue(entity);
                            List<EntityObject> targets = sqlHelper.findBy(targetModel, mappedByField.getName(), id);
                            for (EntityObject target : targets) {
                                mappedByField.setValue(target, entity);
                            }
                            //TODO should also load target's associations
                            a.setValue(entity, targets);
                        } else if (a instanceof ManyToMany) {
                            Object id = idField.getValue(entity);
                            List<EntityObject> targets = sqlHelper.findByRelation(((ManyToMany) a).getMiddleTable(), id);
                            //TODO should also load target's associations
                            a.setValue(entity, targets);
                        } else {
                            throw new PersistenceException("Unknown association type: " + a.getClass());
                        }
                    }
                });
    }

    @Override
    public SqlHelper getSqlHelper() {
        return sqlHelper;
    }

    private EntityModel getEntityModel(EntityObject entity) {
        if (entity instanceof CommonEntityObject) {
            return metadataManager.findEntityByName(((CommonEntityObject) entity).getModelName());
        } else {
            return metadataManager.findEntityByClass(entity.getClass());
        }
    }

    private boolean isPersisted(EntityObject entity) {
        return entity.isPersisted();
    }
}
