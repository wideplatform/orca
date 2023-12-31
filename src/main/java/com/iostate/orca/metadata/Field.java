package com.iostate.orca.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.iostate.orca.metadata.serde.FieldDeserializer;
import com.iostate.orca.metadata.serde.FieldSerializer;

@JsonSerialize(using = FieldSerializer.class)
@JsonDeserialize(using = FieldDeserializer.class)
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
}
