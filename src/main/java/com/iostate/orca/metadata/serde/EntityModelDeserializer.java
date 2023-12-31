package com.iostate.orca.metadata.serde;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.iostate.orca.metadata.EntityModel;
import com.iostate.orca.metadata.Field;

import java.io.IOException;

public class EntityModelDeserializer extends StdDeserializer<EntityModel> {
    protected EntityModelDeserializer(Class<?> vc) {
        super(vc);
    }

    protected EntityModelDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected EntityModelDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public EntityModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode root = p.getCodec().readTree(p);
        String name = root.get("name").asText();
        String tableName = root.get("tableName").asText();
        String idGenerator = root.get("idGenerator").asText();
        ObjectMapper objectMapper = new ObjectMapper();
        Field idField = objectMapper.treeToValue(root.get("idField"), Field.class);
        return new EntityModel(name, tableName, idGenerator, idField);
    }
}
