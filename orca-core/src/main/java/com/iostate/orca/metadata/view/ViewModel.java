package com.iostate.orca.metadata.view;

import com.iostate.orca.api.ViewObject;
import com.iostate.orca.api.exception.PersistenceException;
import com.iostate.orca.core.CommonViewObject;
import com.iostate.orca.metadata.Model;
import com.iostate.orca.metadata.dto.ViewModelDto;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

public class ViewModel extends Model<ViewField> {
    private final String entityModelName;

    public ViewModel(String name, String entityModelName, ViewField idField) {
        super(name, idField);
        this.entityModelName = entityModelName;
    }

    public String getEntityModelName() {
        return entityModelName;
    }

    public ViewObject newInstance() {
        Class<?> linkedClass = linkedClass();
        if (linkedClass != null) {
            try {
                return (ViewObject) linkedClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new PersistenceException("Failed to create an instance for model " + getName(), e);
            }
        } else {
            return new CommonViewObject(this);
        }
    }
    
    public ViewModelDto toDto() {
        ViewModelDto dto = new ViewModelDto();
        dto.setName(getName());
        dto.setEntityModelName(getEntityModelName());
        dto.setIdField(getIdField().toDto());
        dto.setDataFields(getDataFields().stream().map(ViewField::toDto).collect(Collectors.toList()));
        dto.setLinkedClassName(getLinkedClassName());

        return dto;
    }
}
