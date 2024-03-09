package com.iostate.orca.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Model {
    private String name;
    private Field idField;
    private final List<Field> dataFields = Collections.synchronizedList(new ArrayList<>());
    private final transient Map<String, Field> _allFieldsMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private String linkedClassName;
    private transient Class<?> _linkedClass;

    protected Model() {
    }

    protected Model(String name, Field idField) {
        this.name = name;
        this.idField = idField;
    }

    public void addDataField(Field field) {
        if (field.isId()) {
            throw new IllegalStateException(field + " is ID field that should not be added here");
        }
        dataFields.add(field);
    }

    public String getName() {
        return name;
    }

    public Field getIdField() {
        return idField;
    }

    public Collection<Field> getDataFields() {
        return dataFields;
    }

    public void setDataFields(List<Field> dataFields) {
        synchronized (this.dataFields) {
            this.dataFields.clear();
            this.dataFields.addAll(dataFields);
        }
    }

    public String getLinkedClassName() {
        return linkedClassName;
    }

    public void setLinkedClassName(String linkedClassName) {
        this.linkedClassName = linkedClassName;
        _linkedClass = null;
    }

    protected Map<String, Field> allFieldsMap() {
        if (_allFieldsMap.isEmpty()) {
            synchronized (this) {
                if (_allFieldsMap.isEmpty()) {
                    _allFieldsMap.put(idField.getName(), idField);
                    for (Field dataField : getDataFields()) {
                        _allFieldsMap.put(dataField.getName(), dataField);
                    }
                }
            }
        }
        return _allFieldsMap;
    }

    public Collection<Field> allFields() {
        return Collections.unmodifiableCollection(allFieldsMap().values());
    }

    public Class<?> linkedClass() {
        if (linkedClassName == null) {
            return null;
        }

        if (_linkedClass == null) {
            try {
                return Class.forName(linkedClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return _linkedClass;
    }

    public Field findFieldByName(String name) {
        return allFieldsMap().get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(name, model.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
