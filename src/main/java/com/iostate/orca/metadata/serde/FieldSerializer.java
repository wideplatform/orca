package com.iostate.orca.metadata.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iostate.orca.metadata.AssociationField;
import com.iostate.orca.metadata.Field;
import com.iostate.orca.metadata.PluralAssociationField;

import java.io.IOException;

public class FieldSerializer extends StdSerializer<Field> {
    public FieldSerializer() {
        super((Class<Field>) null);
    }

    protected FieldSerializer(Class<Field> t) {
        super(t);
    }

    protected FieldSerializer(JavaType type) {
        super(type);
    }

    protected FieldSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected FieldSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(Field value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", value.getClass().getSimpleName());
        gen.writeStringField("name", value.getName());
        if (value.getColumnName() != null) {
            gen.writeStringField("columnName", value.getColumnName());
        }
        gen.writeStringField("dataType", value.getDataType().name());
        if (value.isId()) {
            gen.writeBooleanField("isId", true);
        }
        gen.writeBooleanField("isNullable", value.isNullable());
        if (value instanceof AssociationField) {
            AssociationField af = (AssociationField) value;
            gen.writeObjectField("targetModel", af.getTargetModel());
            gen.writeObjectField("fetchType", af.getFetchType());
            gen.writeObjectField("cascadeTypes", af.getCascadeTypes());
            if (value instanceof PluralAssociationField) {
                gen.writeObjectField("middleTable", ((PluralAssociationField) value).getMiddleTable());
            }
        }
        gen.writeEndObject();
    }
}
