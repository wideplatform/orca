package com.iostate.orca.metadata.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;

import java.io.IOException;

public class EntityModelSerializer extends StdSerializer<EntityModel> {
    protected EntityModelSerializer(Class<EntityModel> t) {
        super(t);
    }

    protected EntityModelSerializer(JavaType type) {
        super(type);
    }

    protected EntityModelSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected EntityModelSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(EntityModel value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", value.getName());
        gen.writeStringField("tableName", value.getTableName());
        gen.writeStringField("idGenerator", value.getIdGenerator());
        gen.writeObjectField("idField", value.getIdField());
        {
            gen.writeArrayFieldStart("dataFields");
            for (Field dataField : value.getDataFields()) {
                gen.writeObject(dataField);
            }
            gen.writeEndArray();
        }
        gen.writeEndObject();
    }
}
