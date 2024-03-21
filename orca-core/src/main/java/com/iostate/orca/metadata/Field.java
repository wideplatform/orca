package com.iostate.orca.metadata;

import com.iostate.orca.api.EntityObject;
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

    Object getValue(EntityObject entity);

    void setValue(EntityObject entity, Object value);

    /**
     * Internal API.
     */
    void populateValue(EntityObject entity, Object value);

    boolean isUpdated(EntityObject entity);

    FieldDto toDto();
}
