package com.iostate.orca.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iostate.orca.api.CommonEntityObject;
import com.iostate.orca.api.EntityObject;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.metadata.dto.EntityModelDto;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

public class EntityModel extends Model {
    private final String tableName;
    private final String idGenerator;

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

    public EntityObject newInstance() {
        Class<?> linkedClass = linkedClass();
        if (linkedClass != null) {
            try {
                return (EntityObject) linkedClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new PersistenceException("Failed to create an instance for model " + getName(), e);
            }
        } else {
            return new CommonEntityObject(getName());
        }
    }

    public Object getIdValue(EntityObject entity) {
        return getIdField().getValue(entity);
    }

    public final EntityModelDto toDto() {
        EntityModelDto dto = new EntityModelDto();
        dto.setName(getName());
        dto.setTableName(getTableName());
        dto.setIdGenerator(getIdGenerator());
        dto.setIdField(getIdField().toDto());
        dto.setDataFields(getDataFields().stream().map(Field::toDto).collect(Collectors.toList()));
        dto.setLinkedClassName(getLinkedClassName());
        return dto;
    }
}
