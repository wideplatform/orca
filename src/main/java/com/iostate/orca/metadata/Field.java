package com.iostate.orca.metadata;

import com.iostate.orca.api.PersistentObject;
import com.iostate.orca.metadata.dto.FieldDto;

public interface Field {

    String getName();

    boolean hasColumn();

    /**
     * @return null for column-less field (e.g. plural association)
     */
    String getColumnName();

    DataType getDataType();

    boolean isId();

    boolean isNullable();

    boolean isAssociation();

    Object getValue(Object entity);

    void setValue(Object entity, Object value);

    boolean isUpdated(PersistentObject entity);

    FieldDto toDto();
}
