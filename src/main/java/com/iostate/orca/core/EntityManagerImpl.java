package com.iostate.orca.core;

import com.iostate.orca.api.ConnectionProvider;
import com.iostate.orca.api.EntityManager;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.FetchType;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.MetadataManager;
import com.iostate.orca.metadata.HasMany;
import com.iostate.orca.sql.SqlHelper;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EntityManagerImpl implements EntityManager {

    private final MetadataManager metadataManager;

    private final SqlHelper sqlHelper;

    public EntityManagerImpl(MetadataManager metadataManager, ConnectionProvider connectionProvider) {
        this.metadataManager = metadataManager;
        this.sqlHelper = new SqlHelper(connectionProvider, this);
    }

    @Override
    public void persist(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        if (isPersisted(entity)) {
            throw new IllegalStateException("entity is already persisted thus unable to persist");
        }

        EntityModel entityModel = getEntityModel(entity.getClass());
        sqlHelper.insert(entityModel, (PersistentObject) entity);

//    refresh(entity);
    }

    @Override
    public void update(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        if (!isPersisted(entity)) {
            throw new IllegalStateException("entity is not persisted thus unable to update");
        }

        EntityModel entityModel = getEntityModel(entity.getClass());
        sqlHelper.update(entityModel, (PersistentObject) entity);

//    refresh(entity);
    }

    @Override
    public void merge(Object entity) {
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
    public void remove(Class<?> entityClass, Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        EntityModel entityModel = getEntityModel(entityClass);
        sqlHelper.delete(entityModel, id);
    }

    @Override
    public void remove(Object entity) {
        EntityModel entityModel = getEntityModel(entity.getClass());
        Object id = entityModel.getIdField().getValue(entity);
        sqlHelper.delete(entityModel, id);
    }

    @Override
    public void refresh(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        EntityModel entityModel = getEntityModel(entity.getClass());
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
    public <T> T find(Class<T> entityClass, Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        EntityModel entityModel = getEntityModel(entityClass);
        PersistentObject po = find(entityModel, id);

        //noinspection unchecked
        return (T) po;
    }

    @Override
    public PersistentObject find(EntityModel entityModel, Object id) {
        Objects.requireNonNull(entityModel);
        PersistentObject po = sqlHelper.findById(entityModel, id);
        loadAllLazy(entityModel, Collections.singletonList(po));
        return po;
    }

    @Override
    public <T> List<T> findBy(Class<T> entityClass, String objectPath, Object fieldValue) {
        EntityModel entityModel = getEntityModel(entityClass);
        Field field = entityModel.findFieldByName(objectPath);
        List<PersistentObject> pos = sqlHelper.findBy(entityModel, objectPath, fieldValue);
        loadAllLazy(entityModel, pos);
        //noinspection unchecked
        return (List<T>) pos;
    }

    private void loadAllLazy(EntityModel entityModel, List<PersistentObject> pos) {
        Field idField = entityModel.getIdField();
        entityModel.allFields().stream()
                .filter(Field::isAssociation)
                .map(field -> (AssociationField) field)
                .forEach(a -> {
                    if (a.getFetchType() == FetchType.EAGER) {
                        return;
                    }

                    EntityModel targetModel = a.getTargetModelRef().model();
                    Field mappedByField = null;
                    if (a.getMappedByFieldName() != null) {
                        mappedByField = targetModel.findFieldByName(a.getMappedByFieldName());
                    }

                    for (PersistentObject po : pos) {
                        if (a.isSingular()) {
                            Object raw = po.getFieldValue(a.getName());
                            if (raw != null) {
                                Object targetObject = find(targetModel, raw);
                                a.setValue(po, targetObject);
                            }
                        } else {
                            HasMany pa = (HasMany) a;
                            Object id = idField.getValue(po);
                            List<PersistentObject> targets;
                            if (mappedByField != null) {
                                targets = sqlHelper.findBy(targetModel, mappedByField.getName(), id);
                                for (PersistentObject target : targets) {
                                    mappedByField.setValue(target, id);
                                }
                            } else {
                                targets = sqlHelper.findByRelation(pa.getMiddleTable(), id);
                            }
                            //TODO should also load target's associations
                            a.setValue(po, targets);
                        }
                    }
                });
    }

    @Override
    public <T> T fetch(Class<T> entityClass, Object id) {
        return null;
    }

    @Override
    public <T> T ref(Class<T> entityClass, Object id) {
        return null;
    }

    @Override
    public int executeDML(String sql, Object[] args) throws SQLException {
        return sqlHelper.executeDML(sql, args);
    }

    private EntityModel getEntityModel(Class<?> entityClass) {
        return metadataManager.findEntityByClass(entityClass);
    }

    private boolean isPersisted(Object entity) {
        return ((PersistentObject) entity).isPersisted();
    }
}
