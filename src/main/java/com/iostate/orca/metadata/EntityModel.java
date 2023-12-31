package com.iostate.orca.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iostate.orca.api.MapBackedPO;
import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.api.exception.PersistenceException;

import java.lang.reflect.InvocationTargetException;

public class EntityModel extends Model {
    private String tableName;
    private String idGenerator;

    protected EntityModel() {
    }

    public EntityModel(String name, String tableName,
                       String idGenerator, Field idField) {
        super(name, idField);
        this.tableName = tableName;
        this.idGenerator = idGenerator;
    }

    public String getTableName() {
        return tableName;
    }

    @JsonIgnore
    public boolean isIdGenerated() {
        return idGenerator != null && !idGenerator.isEmpty();
    }

    public String getIdGenerator() {
        return idGenerator;
    }

    public PersistentObject newInstance() {
        Class<?> linkedClass = linkedClass();
        if (linkedClass != null) {
            try {
                return (PersistentObject) linkedClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new PersistenceException("Failed to create an instance for model " + getName(), e);
            }
        } else {
            return new MapBackedPO(getName());
        }
    }
}
