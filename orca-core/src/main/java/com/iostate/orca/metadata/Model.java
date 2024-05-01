package com.iostate.orca.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Model<F extends IField> {
    private final String name;
    private final F idField;
    private final List<F> dataFields = Collections.synchronizedList(new ArrayList<>());
    private final transient Map<String, F> _allFieldsMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private String linkedClassName;
    private transient Class<?> _linkedClass;

    protected Model(String name, F idField) {
        this.name = name;
        this.idField = idField;
    }

    public void addDataField(F F) {
        if (F.isId()) {
            throw new IllegalStateException(F + " is ID field that should not be added here");
        }
        dataFields.add(F);
    }

    public String getName() {
        return name;
    }

    public F getIdField() {
        return idField;
    }

    public Collection<F> getDataFields() {
        return dataFields;
    }

    public void setDataFields(List<F> dataFields) {
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

    protected Map<String, F> allFieldsMap() {
        if (_allFieldsMap.isEmpty()) {
            synchronized (this) {
                if (_allFieldsMap.isEmpty()) {
                    _allFieldsMap.put(idField.getName(), idField);
                    for (F dataField : getDataFields()) {
                        _allFieldsMap.put(dataField.getName(), dataField);
                    }
                }
            }
        }
        return _allFieldsMap;
    }

    public Collection<F> allFields() {
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

    public F findFieldByName(String name) {
        return allFieldsMap().get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(name, ((Model<?>) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
