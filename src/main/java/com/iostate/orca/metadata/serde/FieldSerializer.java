package com.iostate.orca.metadata.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iostate.orca.metadata.Field;

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
        gen.writeEndObject();
    }
}
